package com.amor.chatclient.service.vectorstore;

import com.amor.chatclient.repository.VectorStoreDocumentInfoRepository;
import com.amor.chatclient.webui.vectorstore.VectorStoreView;
import com.vaadin.flow.component.notification.Notification;
import lombok.Getter;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class VectorStoreDocumentService {

    public static final String DOCUMENT_SELECTING_EVENT = "DOCUMENT_SELECTING_EVENT";
    public static final String DOCUMENT_ADDING_EVENT = "DOCUMENT_ADDING_EVENT";
    public static final String DOCUMENTS_DELETE_EVENT = "DOCUMENTS_DELETE_EVENT";

    private final Map<String, Supplier<List<Document>>> documentSuppliers = new ConcurrentHashMap<>();

    public record TokenTextSplitInfo(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed,
                                     int maxNumChunks, boolean keepSeparator) {}


    public final static TokenTextSplitInfo DEFAULT_TOKEN_TEXT_SPLIT_INFO =
            new TokenTextSplitInfo(800, 350, 5, 10000, true);

    private final File uploadDir;

    @Getter
    private final DataSize maxUploadSize;

    private final Map<String, TokenTextSplitter> splitters;
    private final TokenTextSplitter defaultTokenTextSplitter;
    private final Map<String, VectorStoreDocumentInfo> documentInfos = new ConcurrentHashMap<>();

    @Getter
    private final PropertyChangeSupport documentInfoChangeSupport;

    private final VectorStoreDocumentInfoRepository documentInfoRepository;

    public VectorStoreDocumentService(VectorStoreDocumentInfoRepository documentInfoRepository, @Value("${spring.servlet.multipart.max-file-size}") DataSize maxUploadSize) {
        this.documentInfoRepository = documentInfoRepository;
        this.uploadDir = new File(System.getProperty("user.home"), "amor/vectorstore");
        if (!uploadDir.exists())
            uploadDir.mkdirs();
        this.maxUploadSize = maxUploadSize;
        this.splitters = new WeakHashMap<>();
        this.defaultTokenTextSplitter = newTokenTextSplitter(DEFAULT_TOKEN_TEXT_SPLIT_INFO);
        this.documentInfoChangeSupport = new PropertyChangeSupport(this);
        // 初始化时从MongoDB加载所有文档信息
        documentInfoRepository.findAll().forEach(info -> {
            documentInfos.put(info.getDocInfoId(), info);
            documentSuppliers.put(info.getDocInfoId(), createLazySupplier(info.getDocumentPath()));
        });
    }

    public VectorStoreDocumentInfo putNewDocument(String documentFileName, List<Document> uploadedDocumentItems) {
        long createTimestamp = System.currentTimeMillis();
        File uploadedDocumentFile = new File(uploadDir, documentFileName);
        String docInfoId = VectorStoreView.DOC_INFO_ID + "-" + UUID.randomUUID();
        List<Document> documentList = IntStream.range(0, uploadedDocumentItems.size()).boxed()
                .map(i -> copyNewDocument(docInfoId, i, uploadedDocumentItems.get(i))).toList();
        VectorStoreDocumentInfo vectorStoreDocumentInfo =
                new VectorStoreDocumentInfo(docInfoId, uploadedDocumentFile.getName(), createTimestamp, createTimestamp,
                        uploadedDocumentFile.getPath());
        this.documentInfos.put(docInfoId, vectorStoreDocumentInfo);
        documentSuppliers.put(docInfoId, () -> documentList);
        documentInfoRepository.save(vectorStoreDocumentInfo);
        documentInfoChangeSupport.firePropertyChange(DOCUMENT_ADDING_EVENT, null, List.of(vectorStoreDocumentInfo));
        return vectorStoreDocumentInfo;
    }

    // 新增懒加载方法
    private Supplier<List<Document>> createLazySupplier(String documentPath) {
        return new Supplier<>() {
            private volatile List<Document> cachedDocuments = null;

            @Override
            public List<Document> get() {
                if (cachedDocuments == null) {
                    synchronized (this) {
                        if (cachedDocuments == null) {
                            try {
                                File documentFile = new File(documentPath);
                                if (documentFile.exists()) {
                                    Resource resource = new FileSystemResource(documentFile);
                                    cachedDocuments = split(resource);
                                } else {
                                    cachedDocuments = Collections.emptyList();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                cachedDocuments = Collections.emptyList();
                            }
                        }
                    }
                }
                return cachedDocuments;
            }
        };
    }

    private Document copyNewDocument(String docInfoId, Integer index, Document uploadedDocument) {
        Map<String, Object> metadata = new HashMap<>(uploadedDocument.getMetadata());
        metadata.put(VectorStoreView.DOC_INFO_ID, docInfoId);
        return new Document(index + "-" + docInfoId, uploadedDocument.getText(), metadata);
    }

    public Map<String, List<Document>> extractDocumentItems(List<String> uploadedFileNames) {
        return uploadedFileNames.stream()
                .map(fileName -> Map.entry(fileName, split(new FileSystemResource(new File(uploadDir,
                        fileName))))).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<Document> split(Resource resource) {
        return split(this.defaultTokenTextSplitter, new TikaDocumentReader(resource));
    }

    private List<Document> split(TextSplitter textSplitter, DocumentReader documentReader) {
        return textSplitter.split(documentReader.read());
    }

    public List<Document> split(Resource resource, TokenTextSplitInfo tokenTextSplitInfo) {
        return split(this.splitters.computeIfAbsent(tokenTextSplitInfo.toString(),
                key -> newTokenTextSplitter(tokenTextSplitInfo)), new TikaDocumentReader(resource));
    }

    private TokenTextSplitter newTokenTextSplitter(TokenTextSplitInfo tokenTextSplitInfo) {
        return new TokenTextSplitter(tokenTextSplitInfo.chunkSize(), tokenTextSplitInfo.minChunkSizeChars(),
                tokenTextSplitInfo.minChunkLengthToEmbed(), tokenTextSplitInfo.maxNumChunks(),
                tokenTextSplitInfo.keepSeparator());
    }

    public void addUploadedDocumentFile(String fileName, File uploadedFile) throws Exception {
        File file = new File(uploadDir, fileName);
        if (file.exists())
            throw new FileAlreadyExistsException("Already Exists - " + file.getAbsolutePath());
        Files.copy(uploadedFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Notification.show("File uploaded successfully to: " + fileName);
    }

    public void removeUploadedDocumentFile(String fileName) throws IOException {
        Files.deleteIfExists(new File(uploadDir, fileName).toPath());
    }

    public VectorStoreDocumentInfo updateDocumentInfo(VectorStoreDocumentInfo vectorStoreDocumentInfo, String title) {
        VectorStoreDocumentInfo updatedInfo = vectorStoreDocumentInfo.newTitle(title);
        documentInfos.put(updatedInfo.getDocInfoId(), updatedInfo);
        documentInfoRepository.save(updatedInfo);

        documentInfoChangeSupport.firePropertyChange(DOCUMENT_ADDING_EVENT, List.of(vectorStoreDocumentInfo), List.of(updatedInfo));

        return updatedInfo;
    }

    public void deleteDocumentInfo(String docId) {
        VectorStoreDocumentInfo removed = documentInfos.remove(docId);
        documentSuppliers.remove(docId);
        documentInfoRepository.deleteById(docId);

        documentInfoChangeSupport.firePropertyChange(DOCUMENTS_DELETE_EVENT, removed, null);
    }

    public List<VectorStoreDocumentInfo> getDocumentList() {
        reloadFromDatabase();
        return documentInfos.values().stream()
                .sorted(Comparator.comparingLong(VectorStoreDocumentInfo::getUpdateTimestamp).reversed())
                .toList();
    }

    private synchronized void reloadFromDatabase() {
        // 清空再重新加载
        documentInfos.clear();
        documentSuppliers.clear();
        documentInfoRepository.findAll().forEach(info -> {
            documentInfos.put(info.getDocInfoId(), info);
            documentSuppliers.put(info.getDocInfoId(), createLazySupplier(info.getDocumentPath()));
        });
    }

    public Supplier<List<Document>> getDocumentSupplier(String docInfoId) {
        return documentSuppliers.get(docInfoId);
    }

}

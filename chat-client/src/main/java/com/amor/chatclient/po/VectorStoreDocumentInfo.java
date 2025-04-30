package com.amor.chatclient.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vector_store_documents")
@Data
public class VectorStoreDocumentInfo {

    @Id
    private String docInfoId;

    private String title;
    private long createTimestamp;
    private long updateTimestamp;
    private String documentPath;

    public VectorStoreDocumentInfo() {
        // MongoDB 需要无参构造器
    }

    public VectorStoreDocumentInfo(String docInfoId, String title, long createTimestamp, long updateTimestamp, String documentPath) {
        this.docInfoId = docInfoId;
        this.title = title;
        this.createTimestamp = createTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.documentPath = documentPath;
    }

    public VectorStoreDocumentInfo newTitle(String newTitle) {
        return new VectorStoreDocumentInfo(docInfoId, newTitle, createTimestamp, System.currentTimeMillis(), documentPath);
    }

    public VectorStoreDocumentInfo newUpdateTimestamp() {
        return new VectorStoreDocumentInfo(docInfoId, title, createTimestamp, System.currentTimeMillis(), documentPath);
    }
}
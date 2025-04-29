package com.amor.chatclient.repository;

import com.amor.chatclient.service.vectorstore.VectorStoreDocumentInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VectorStoreDocumentInfoRepository extends MongoRepository<VectorStoreDocumentInfo, String> {
}

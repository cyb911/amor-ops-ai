package com.amor.chatclient.repository;

import com.amor.chatclient.po.VectorStoreDocumentInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VectorStoreDocumentInfoRepository extends MongoRepository<VectorStoreDocumentInfo, String> {
}

package com.amor.mcpservice.repository;

import com.amor.mcpservice.po.SysToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpenApiRepository extends MongoRepository<SysToken, String> {

    SysToken findByKey(String key);
}

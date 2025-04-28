package com.amor.mcpservice.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "open_api_info")
public class OpenApi {
    @Id
    private String key;

    private String token;

    @Indexed(expireAfter = "0s")
    private Date expireAt;
}

package com.amor.chatclient.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.mcp.client.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.mcp.client.autoconfigure.properties.McpClientCommonProperties;
import org.springframework.ai.mcp.client.autoconfigure.properties.McpSseClientProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({ McpSseClientProperties.class, McpClientCommonProperties.class })
@ConditionalOnProperty(prefix = McpClientCommonProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class CustomSseHttpClientTransportAutoConfiguration {
    @Bean
    public List<NamedClientMcpTransport> mcpHttpClientTransports(McpSseClientProperties sseProperties,
                                                                 ObjectProvider<ObjectMapper> objectMapperProvider) {

        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        List<NamedClientMcpTransport> sseTransports = new ArrayList<>();

        for (Map.Entry<String, McpSseClientProperties.SseParameters> entry : sseProperties.getConnections().entrySet()) {
            String name = entry.getKey();
            String fullUrl = entry.getValue().url();

            // 拆分 baseUri 和 endpoint
            URI uri = URI.create(fullUrl);
            String baseUri = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                baseUri += ":" + uri.getPort();
            }

            String sseEndpoint = uri.getRawPath();
            if (uri.getRawQuery() != null) {
                sseEndpoint += "?" + uri.getRawQuery();
            }

            if(StrUtil.isBlank(sseEndpoint)) {
                sseEndpoint = "/sse";
            }

            // 使用 builder 构建 transport，避免 deprecated 构造器
            HttpClientSseClientTransport transport = HttpClientSseClientTransport.builder(baseUri)
                    .sseEndpoint(sseEndpoint)
                    .objectMapper(objectMapper)
                    .build();

            sseTransports.add(new NamedClientMcpTransport(name, transport));
        }

        return sseTransports;
    }
}

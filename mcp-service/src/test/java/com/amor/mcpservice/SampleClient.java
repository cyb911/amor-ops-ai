package com.amor.mcpservice;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class SampleClient {
    private final McpClientTransport transport;

    public SampleClient(McpClientTransport transport) {
        this.transport = transport;
    }

    public void run() {

        var client = McpClient.sync(this.transport).build();

        client.initialize();

        client.ping();

        // List and demonstrate tools
        McpSchema.ListToolsResult toolsList = client.listTools();
        System.out.println("Available Tools = " + toolsList);

        McpSchema.CallToolResult alertResult = client.callTool(new McpSchema.CallToolRequest("getWeatherByCity", Map.of("city", "上海")));
        System.out.println("Alert Response = " + alertResult);

        client.closeGracefully();

    }
}

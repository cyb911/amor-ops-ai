package com.amor.mcpservice;

import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;

public class ClientSse {
    public static void main(String[] args) {
        var transport = new HttpClientSseClientTransport("http://localhost:8090");
        new SampleClient(transport).run();
    }

}

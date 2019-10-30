package com.hewecode.rsocketrpc.client;

import com.hewecode.rsocketrpc.Server;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;

import java.net.URI;

/**
 * Small class to reduce boilerplate
 */
class FactoryForClient {

    public static PingPongServiceClient createActiveClient() {
        if(Server.usesWebsocket()) {
            return viaWebsocket();
        } else {
            return viaTCP();
        }
    }
    public static PingPongServiceClient viaWebsocket() {
        return createClientWith(WebsocketClientTransport.create(URI.create("wb://localhost:"+ Server.port)));
    }

    public static PingPongServiceClient viaTCP() {
       return createClientWith(TcpClientTransport.create("localhost", Server.port));
    }

    private static PingPongServiceClient createClientWith(ClientTransport transport) {
        RSocket rSocket = RSocketFactory
                .connect()
                .transport(transport)
                .start()
                .block();
        return new PingPongServiceClient(rSocket);
    }
}

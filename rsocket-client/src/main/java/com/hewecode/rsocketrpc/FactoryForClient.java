package com.hewecode.rsocketrpc;

import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Small class to reduce boilerplate
 */
class FactoryForClient {
    private static final Logger logger = LoggerFactory.getLogger(FactoryForClient.class);
    private static final String rsocketHost = System.getProperty("rsocketHost", "127.0.0.1");
    private static final int rsocketPort = Integer.parseInt(System.getProperty("rsocketPort", "7777"));
    private static final boolean useWebSockets = Boolean.parseBoolean(System.getProperty("rsocketUseWebsockets", "true"));

    public static PingPongServiceClient createActiveClient() {
        logger.info("Creating client and connecting to "+rsocketHost+" via port: "+rsocketPort+". Using "+(useWebSockets?"websockets":"tcp"));
        if(useWebSockets) {
            return viaWebsocket();
        } else {
            return viaTCP();
        }
    }
    public static PingPongServiceClient viaWebsocket() {

        return createClientWith(WebsocketClientTransport.create(URI.create("wb://"+rsocketHost+":"+ rsocketPort)));
    }

    public static PingPongServiceClient viaTCP() {
       return createClientWith(TcpClientTransport.create(rsocketHost, rsocketPort));
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

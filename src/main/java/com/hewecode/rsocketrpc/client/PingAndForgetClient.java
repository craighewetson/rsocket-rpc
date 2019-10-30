package com.hewecode.rsocketrpc.client;

import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PingAndForgetClient {
    private static final Logger logger = LoggerFactory.getLogger(PingAndForgetClient.class);

    public static void main(String[] args) {
        logger.info("Starting fire and forget client To request the server to stop");
        PingPongServiceClient serviceClient = FactoryForClient.createActiveClient();

        // made server shutdown when a ball called: "stop" is received
        serviceClient.pingAndForget(Ping.newBuilder().setBall("stop").build()).block(Duration.ofSeconds(10));

        logger.info("DONE. Fired a stop message to server");
    }
}

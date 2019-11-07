package com.hewecode.rsocketrpc;

import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import com.hewecode.rsocketrpc.pingpong.protobuf.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class SinglePingPongClient {
    private static final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(SinglePingPongClient.class);

    public static void main(String[] args) {
        logger.info("Starting request response client");
        PingPongServiceClient serviceClient = FactoryForClient.createActiveClient();

        logger.info("Calling and waiting with a block");
        final Ping msg = Ping.newBuilder().setBall("ball "+ random.nextInt(100)).build();

        Pong pong = serviceClient.ping(msg).block();
        logger.info("got pong "+pong.getBall()+"'. Shutting down");
    }
}

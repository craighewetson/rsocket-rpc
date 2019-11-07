package com.hewecode.rsocketrpc;

import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import com.hewecode.rsocketrpc.pingpong.protobuf.Pong;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PingsAndSinglePongClient {
    private static final Logger logger = LoggerFactory.getLogger(SinglePingLotsOfPongsClient.class);

    public static void main(String[] args) {
        logger.info("Starting 'Lots of Pings Single Pong' client");

        PingPongServiceClient serviceClient = FactoryForClient.createActiveClient();

        List<Ping> pings = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            final Ping ping = Ping.newBuilder().setBall("ping ball "+ i).build();
            pings.add(ping);
        }

        // call server
        logger.info("Calling lotsOfPings with a flux of pings "+pings.size()+" long");
        Publisher<Ping> pingPublisher = Flux.fromIterable(pings).log()
                .delayElements(Duration.ofMillis(100))
                .take(1);

        Mono<Pong> pongResponse = serviceClient.lotsOfPings(pingPublisher);
        logger.info("Call made now waiting for single pong response...");
        Pong pong = pongResponse.block();//Duration.ofSeconds(30));
        logger.info("got pong: "+pong+" from server. Shutting down");
    }
}

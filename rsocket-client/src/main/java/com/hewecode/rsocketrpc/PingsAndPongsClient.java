package com.hewecode.rsocketrpc;

import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import com.hewecode.rsocketrpc.pingpong.protobuf.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PingsAndPongsClient {
    private static final Logger logger = LoggerFactory.getLogger(PingsAndPongsClient.class);
    private static final AtomicBoolean pongsDone = new AtomicBoolean(false);
    private static final AtomicBoolean pingsDone = new AtomicBoolean(false);

    public static void main(String[] args) {
        logger.info("Starting 'Lots of Pings and logs of Pongs' client");

        PingPongServiceClient serviceClient = FactoryForClient.createActiveClient();

        // call server
        int maxPing = 10;

        logger.info("Calling lotsOfPingsAndPongs with "+maxPing+" pings and expecting lots of pongs asynchronously");
        Flux<Ping> pings = Flux.generate(
                AtomicLong::new, // initial value of the "state" object
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next(Ping.newBuilder().setBall("Ping "+i).build());
                    logger.info(">> sent ping "+i);
                    if (i == maxPing) {
                        sink.complete();
                        pingsDone.set(true);
                    }
                    return state;
                },
                (state) -> logger.info("Finished sending stuff from client for (Ping "+state+")"));

        //+++++++++++++++++++

        Flux<Pong> pongs = serviceClient.lotsOfPingsAndPongs(pings);

        //+++++++++++++++++++

        final AtomicInteger pongCount = new AtomicInteger();
        pongs.subscribe(pong -> {
            logger.info("<< onNext: "+pong);
            pongCount.incrementAndGet();
        }, throwable -> {
            logger.error("onError: ", throwable);
        }, () -> {
            logger.info("onCompleted: "+pongCount+" pongs");
            pongsDone.set(true);
        });

        while(!pongsDone.get() || !pingsDone.get()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                logger.error("Failed to sleep", e);
            }
        }
    }
}

package com.hewecode.rsocketrpc.client;

import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceClient;
import com.hewecode.rsocketrpc.pingpong.protobuf.Pong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SinglePingLotsOfPongsClient {
    private static final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(SinglePingLotsOfPongsClient.class);
    private static final AtomicInteger pongCount = new AtomicInteger(0);
    private static final AtomicBoolean done = new AtomicBoolean(false);

    private static int cancelAfterNPongs = 3;

    public static void main(String[] args) {
        logger.info("Starting 'Lots of Pongs' client");
        PingPongServiceClient serviceClient = FactoryForClient.createActiveClient();

        logger.info("Calling and waiting with a block");
        final Ping ping = Ping.newBuilder().setBall("ball "+ random.nextInt(100)).build();

        // call server
        Flux<Pong> pongs = serviceClient.lotsOfPongs(ping);
        logger.info("sent ping to server now going to process the pongs that were received");

        Disposable consumerHandle = pongs.subscribe(pong -> {
            logger.info("onNext: "+pong);
            pongCount.incrementAndGet();
        }, throwable -> {
            logger.error("onError: ", throwable);
        }, () -> {
            logger.info("onCompleted: "+pongCount.get()+" pongs");
            done.set(true);
        });

        waitUntilDone(consumerHandle);

        logger.info("got "+pongCount.get()+"s '. Shutting down");
    }

    private static void waitUntilDone(Disposable consumerHandle) {
        while(!done.get()) {
            try {
                Thread.sleep(100);
                // testing cancel functionality:
                if(pongCount.get() > cancelAfterNPongs) {
                    logger.info("cancelling");
                    consumerHandle.dispose();
                    done.set(true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

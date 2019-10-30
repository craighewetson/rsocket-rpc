package com.hewecode.rsocketrpc;

import com.google.protobuf.Empty;
import com.hewecode.rsocketrpc.pingpong.protobuf.Ping;
import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongService;
import com.hewecode.rsocketrpc.pingpong.protobuf.Pong;
import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class PingPongServiceImpl implements PingPongService {
    private static final Logger logger = LoggerFactory.getLogger(PingPongServiceImpl.class);

    @Override
    public Mono<Pong> ping(Ping message, ByteBuf metadata) {
        logger.info("Ping received: " + message);
        Pong pong = Pong.newBuilder().setBall("Pong your "+ message.getBall()).build();
        logger.info("Replying with: " + message);
        return Mono.just(pong);
    }

    @Override
    public Mono<Empty> pingAndForget(Ping message, ByteBuf metadata) {
        logger.info("Got the fired message: "+message);
        if(message.getBall().equalsIgnoreCase("stop")) {
            logger.info("Got stop message");
            stopped = true;
        }
        return Mono.empty();
    }

    @Override
    public Flux<Pong> lotsOfPongs(Ping message, ByteBuf metadata) {
        logger.info("Got request for pongs: "+message);
        List<Pong> pongs = new ArrayList<>();
        for(int ballCount = 0; ballCount < 20; ballCount++) {
            pongs.add(Pong.newBuilder().setBall("Ball" + ballCount).build());
        }
        logger.info("Ponging now with "+pongs.size()+" balls");
        return Flux.fromIterable(pongs).delayElements(Duration.ofMillis(100));
    }

    @Override
    public Mono<Pong> lotsOfPings(Publisher<Ping> messages, ByteBuf metadata) {
        logger.info("Incomming stream of pings will all be processed and a single pong will be returned");
        List<Ping> pings = new ArrayList<>();

        final PingChomper pingChomper = new PingChomper(1);

        messages.subscribe(pingChomper);

        pingChomper.waitForDone();

        return Mono.just(Pong.newBuilder().setBall("Ball from "+pings.size()+" pings").build()).log();
    }

// read from:   http://sagan-production.cfapps.io/blog/2016/06/13/notes-on-reactive-programming-part-ii-writing-some-code#threads-schedulers-and-background-processing

    @Override
    public Flux<Pong> lotsOfPingsAndPongs(Publisher<Ping> messages, ByteBuf metadata) {
        logger.info("Incomming stream of pings, at the same time an outgoing stream of pongs");

        final PingChomper pingChomper = new PingChomper(1);

        messages.subscribe(pingChomper);

        //TODO check this out?
        return Flux.generate(
                AtomicLong::new, // initial value of the "state" object
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sink.next(Pong.newBuilder().setBall("Pong "+i).build());
                    if (i == 20) {
                        sink.complete();
                    }
                    return state;
                },
                (state) -> logger.info("done ... want to close db connections here? after pong "+state));
    }

    private class PingChomper implements Subscriber<Ping> {
        private List<Ping> chompedPings = new ArrayList<>();
        private final RunningState running = new RunningState();
        private final int pingsPerChomp;
        private Subscription subscription;
        /**
         * @param pingsPerChomp The number this subscriber is going to request from the publisher each time
         */
        PingChomper(int pingsPerChomp) {
            this.pingsPerChomp = pingsPerChomp;
        }

        @Override
        public void onSubscribe(Subscription s) {
            subscription = s;
            logger.info("onSubscribe ");
            s.request(pingsPerChomp); // this must be done or else it will hang
        }

        @Override
        public void onNext(Ping ping) {
            chompedPings.add(ping);
            logger.info(">> onNext: " + ping);
            subscription.request(pingsPerChomp);
        }

        @Override
        public void onError(Throwable t) {
            logger.error(">> onError: ", t);
            running.done();
        }

        @Override
        public void onComplete() {
            logger.info(">> onCompleted: " + chompedPings.size() + " pings");
            running.done();
        }

        void waitForDone() {
            running.waitForDone();
        }
    }

    // ========= not part of the normal implementation
    private volatile boolean stopped = false;
    public boolean gotStop() {
        return stopped;
    }

    private class RunningState {
        private final AtomicBoolean done = new AtomicBoolean(false);

        private void done(){
            done.set(true);
        }

        private void waitForDone(){
            while(!done.get()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    logger.error("Failed to sleep", e);
                }
            }
            logger.info("request done!");
        }
    }
}

package com.hewecode.rsocketrpc;

import com.hewecode.rsocketrpc.pingpong.protobuf.PingPongServiceServer;
import io.rsocket.RSocketFactory;
import io.rsocket.rpc.rsocket.RequestHandlingRSocket;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public static final int port = 7777;
    private static final boolean useWebsocket = true;

    public static void main(String[] args) {
        logger.info("Server is starting up...");
        PingPongServiceImpl serviceImpl = new PingPongServiceImpl();
        final PingPongServiceServer serviceServer = new PingPongServiceServer(serviceImpl, Optional.empty(), Optional.empty());

        CloseableChannel closeableChannel =
                RSocketFactory
                .receive()
                .acceptor((setup, sendingSocket) -> Mono.just(new RequestHandlingRSocket(serviceServer)))
                 .transport(useWebsocket?WebsocketServerTransport.create(port):TcpServerTransport.create(port))
                .start()
                .block();

        logger.info("Server has started. Waiting for pings...");

        while(!serviceImpl.gotStop()) {
            if(closeableChannel!=null && closeableChannel.isDisposed()) {
                logger.info("The channel was disposed");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Server thread failed to sleep a bit", e);
            }
        }

        logger.info("Server shutdown");
    }

    /**
     * A method for convenience to help with demoing different transports
     * @return
     */
    public static boolean usesWebsocket() {
       return useWebsocket;
    }
}

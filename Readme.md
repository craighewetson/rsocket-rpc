# Ping Pong Service
This is a project to demonstrate the capabilities of:
* RSocket RPC
* more to come, see [roadmap](#roadmap)

# RSocket RPC IDL Background
[RSocket RPC](https://github.com/rsocket/rsocket-rpc-java) (like [gRPC](https://grpc.io/)) uses protocol buffers as the Interface Definition Language (IDL) 
for describing both the service interface and the structure of the payload messages. 

This IDL is essentially text and is shared by communicating parties (client, server etc) and using
RSocket RPC libraries the client and server can be generated in various programming languages. 
Java being the one that was chosen for this project.

[See PingPongService IDL](src/main/proto/Service.proto)

# Building
./gradlew clean build

# Running 
To run a server instance of the service:
./gradlew startServer

The following clients are available to demonstrate capabilities:
* PingAndForgetClient (./gradlew startPingAndForgetClient)
Starts up a client which will send a single ping to the server and shutdown. 
The server is configured programmatically shutdown when this happens. 
* SinglePingAndPongClient (./gradlew startSinglePingAndPongClient)
Starts up a client which will send a single ping and wait for a single pong from the server.
* SinglePingAndLotsOfPongsClient (./gradlew startSinglePingAndLotsOfPongsClient)
Starts up a client which will send a single ping to the server and get multiple pongs back

The following clients are still a work in progress:
* PingsAndPongsClient - WIP
* PingsAndSinglePongClient - WIP

# Roadmap
* Get project working with [JIP]((https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin) ), to dockerize the app 
* Host service within [Micronaut](https://micronaut.io/)
* Complete PingPong Service/Client project
  * Two streaming options are not working yet
 
  

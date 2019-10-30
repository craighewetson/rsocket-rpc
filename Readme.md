# Ping Pong Service
This is a basic project to demonstrate the capabilities of:
* RSocket RPC
* (more to come, see roadmap section)

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
* Get project working with JIP, to dockerize the app
  (https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#quickstart)  
* Host service within Micronaut
* Complete PingPong Service/Client project
  * Two streaming options are not working yet
 
  

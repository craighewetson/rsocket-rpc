# building
```shell script
./gradlew jibDockerBuild
```


# Running dockerized version of application
```shell
 docker run -d -p 7777:7777 rsocket-server:0.1
```
 
Remember to change the ports to match that of what you run the server and client with
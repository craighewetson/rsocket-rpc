# building
```shell
./gradlew jibDockerBuild
```

# Running dockerized version of application
```shell
 docker run -d -p 8080:8080 micronaut-server:0.1
```
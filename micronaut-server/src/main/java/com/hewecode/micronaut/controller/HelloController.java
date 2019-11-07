package com.hewecode.micronaut.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller("/hello") // <1>
public class HelloController {
    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Get("/") // <2>
    @Produces(MediaType.TEXT_PLAIN) // <3>
    public String index() {
        log.info("got request on hello controller");
        return "Hello World"; // <4>
    }
}

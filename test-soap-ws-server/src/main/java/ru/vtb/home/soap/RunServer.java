package ru.vtb.home.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RunServer {

    public static void main(String[] args) {
        SpringApplication.run(RunServer.class);
        // http://localhost:8080/ws/beers.wsdl
        // http://minishift.host:8080/ws/beers.wsdl
    }

}
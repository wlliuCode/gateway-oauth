package com.wlliu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class OauthApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApiApplication.class,args);
    }
}

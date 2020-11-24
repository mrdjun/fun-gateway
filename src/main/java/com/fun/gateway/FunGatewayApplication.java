package com.fun.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author MrDJun 2020/10/26
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FunGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(FunGatewayApplication.class, args);
    }
}

package com.spring.cloud.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zhangjj
 * @create 2018-11-08 14:35
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceDiscoverApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoverApplication.class);
    }
}

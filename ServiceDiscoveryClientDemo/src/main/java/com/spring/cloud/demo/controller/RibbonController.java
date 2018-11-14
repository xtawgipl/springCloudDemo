package com.spring.cloud.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Ribbon负载均衡
 *
 * @author zhangjj
 * @create 2018-11-14 14:09
 **/
@RestController
public class RibbonController {

    private Logger logger = LoggerFactory.getLogger(RibbonController.class);

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @GetMapping("/lb/say")
    public String say(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("service-discovery-demo");
        logger.info("uri = " + serviceInstance.getUri());
        ResponseEntity<String> forEntity = restTemplate.getForEntity(serviceInstance.getUri() + "/work", String.class);
        return forEntity.getBody();
    }
}

package com.spring.cloud.demo.controller;

import com.spring.cloud.demo.annotion.CustomizedLoadBalanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

/**
 * 原生调用 ，利用restTemplate 调用 和 实现 负载
 * @author zhangjj
 * @create 2018-11-08 17:21
 **/
@RestController
public class NativeController {

    @Autowired
    private DiscoveryClient discoveryClient;

//    @Resource(name = "restTemplate")
    @Autowired
    @CustomizedLoadBalanced
    private RestTemplate restTemplate;

    @Resource(name = "lbRestTemplate")
    private RestTemplate lbRestTemplate;

    /**
     * 写死调用一个服务的一个接口
     * @return
     * @throws MalformedURLException
     */
    @GetMapping("/say")
    public String say() throws MalformedURLException {
//        List<String> services = discoveryClient.getServices();
        List<ServiceInstance> instances = discoveryClient.getInstances("service-discovery-demo");
        String url = instances.get(0).getUri().toURL().toString() + "/work";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        System.out.println("调用 ：" + url);
        return forEntity.getBody();
    }

    /**
     * 模拟负载均衡
     * @return
     * @throws MalformedURLException
     */
    @GetMapping("/say1")
    public String say1() throws MalformedURLException {
        List<ServiceInstance> instances = discoveryClient.getInstances("service-discovery-demo");
        Random random = new Random();
        int index = random.nextInt(instances.size());
        String url = instances.get(index).getUri().toURL().toString() + "/work";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        System.out.println("调用 ：" + url);
        return forEntity.getBody();
    }

    @GetMapping("/say2")
    public String say2() throws MalformedURLException {
        String url = "/work";
        ResponseEntity<String> forEntity = lbRestTemplate.getForEntity(url, String.class);
        System.out.println("调用 ：" + url);
        return forEntity.getBody();
    }
}

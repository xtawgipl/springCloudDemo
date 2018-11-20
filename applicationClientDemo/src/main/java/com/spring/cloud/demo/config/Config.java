package com.spring.cloud.demo.config;

import com.spring.cloud.demo.annotion.CustomizedLoadBalanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjj
 * @create 2018-11-08 16:58
 **/
@Configuration
public class Config {

    /**
     * 可以用 注解 + Autowired 注入
     * 也可以用resource 用name 指定注入
     */
    @CustomizedLoadBalanced
    @Bean/*(name = "restTemplate")*/
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean(name = "lbRestTemplate")
    public RestTemplate buildLBRestTemplate(SimpleClientHttpRequestInterceptor simpleClientHttpRequestInterceptor){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(simpleClientHttpRequestInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}

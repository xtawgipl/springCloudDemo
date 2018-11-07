package com.spring.cloud.config.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjj
 * @create 2018-11-02 16:30
 **/
@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigApplication.class, args);
    }

    /*@Bean
    public EnvironmentRepository environmentRepository() {
        return (String application, String profile, String label) -> {
            Environment environment = new Environment("default", profile);
            List<PropertySource> propertySources = environment.getPropertySources();
            Map<String, Object> source = new HashMap<>();
            source.put("name", "自定义配置中心");
            source.put("desc", "自定义配置优先（其他配置失效）");
            PropertySource propertySource = new PropertySource("map", source);
            // 追加 PropertySource
            propertySources.add(propertySource);
            return environment;
        };
    }*/
}

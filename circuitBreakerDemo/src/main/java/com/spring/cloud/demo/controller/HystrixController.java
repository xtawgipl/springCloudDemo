package com.spring.cloud.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

/**
 * @author zhangjj
 * @create 2018-11-21 11:24
 **/
@Controller
public class HystrixController {

    private Logger logger = LoggerFactory.getLogger(HystrixController.class);

    @HystrixCommand(fallbackMethod="failError",
                        commandProperties = {
                                            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                                                    value = "100")})
    @GetMapping("/work5")
    @ResponseBody
    public String work() throws InterruptedException {

        int time = new Random().nextInt(200);
        logger.info("开始调用业务逻辑 。。。{}", time);
        Thread.sleep(time);
        logger.info("结束调用业务逻辑 。。。");
        return "work.........";
    }

    public String failError(){

        return "hystrix circuit....";
    }
}

package com.spring.cloud.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangjj
 * @create 2018-11-08 14:43
 **/
@RestController
public class ServiceController {

    @GetMapping("/work")
    public String work(){
        return "working ...";
    }
}

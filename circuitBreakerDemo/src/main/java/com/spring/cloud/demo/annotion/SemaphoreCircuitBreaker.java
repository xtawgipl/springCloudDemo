package com.spring.cloud.demo.annotion;

import java.lang.annotation.*;

/**
 * 基于信号量实现熔断
 *
 * @author zhangjj
 * @create 2018-11-15 17:11
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SemaphoreCircuitBreaker {

    /** 同时可处理的任务量 */
    int value();
}

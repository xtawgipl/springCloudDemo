package com.spring.cloud.demo.annotion;

import java.lang.annotation.*;

/**
 * 限流熔断
 *
 * @author zhangjj
 * @create 2018-11-16 11:34
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LimiterCiruitBreaker {

    /** 每秒支撑的访问量 */
    int limit();

    /** 限流周期，默认一分钟 */
    int interval() default 60*1000;
}

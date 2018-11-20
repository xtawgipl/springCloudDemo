package com.spring.cloud.demo.annotion;

import java.lang.annotation.*;

/**
 * @author zhangjj
 * @create 2018-11-15 14:54
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeoutCiruitBreaker {

    /** 超时时间，秒 */
    long timeout() default 3000;

}

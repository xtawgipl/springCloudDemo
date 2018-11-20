package com.spring.cloud.demo.limiter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流器
 *
 * @author zhangjj
 * @create 2018-11-16 14:21
 **/
public class Limiter {

    /** 周期，多长时间段内限流 ，如一分钟 则为一分钟内最多限制多少流量 */
    private long interval;

    /** 第一次访问的时间 */
    private long lastResetTime;

    /** 当前周期内剩余可处理流量 */
    private AtomicInteger limit;

    /** 周期内允许的最大流量 */
    private Integer total;

    public Limiter(long interval, AtomicInteger limit, Integer total) {
        this.interval = interval;
        this.limit = limit;
        this.total = total;
        this.lastResetTime = System.currentTimeMillis();
    }

    public boolean isAllowable(){
        long currentTime = System.currentTimeMillis();
        if(currentTime > lastResetTime + interval){
            reset();
        }
        int value = limit.get();
        System.out.println("value = " + value);
        while(value > 0 && !limit.compareAndSet(value, value - 1)){
            value = limit.get();
            if(value <= 0){
                return false;
            }
        }
        if(value > 0){
            return true;
        }else{
            return false;
        }
    }


    private void reset(){
        this.limit.set(total);
        this.lastResetTime = System.currentTimeMillis();
    }
}

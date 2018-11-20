package com.spring.cloud.demo.aop;

import com.spring.cloud.demo.annotion.LimiterCiruitBreaker;
import com.spring.cloud.demo.annotion.SemaphoreCircuitBreaker;
import com.spring.cloud.demo.annotion.TimeoutCiruitBreaker;
import com.spring.cloud.demo.limiter.Limiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 拦截熔断
 *
 * @author zhangjj
 * @create 2018-11-15 15:09
 **/
@Component
@Aspect
public class CiruitBreakerAspect {

    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    private ConcurrentHashMap<Method, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Method, Limiter> limiterMap = new ConcurrentHashMap<>();

    @Around("@annotation(com.spring.cloud.demo.annotion.TimeoutCiruitBreaker)")
    public Object advancedSayInTimeout(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        TimeoutCiruitBreaker annotation = signature.getMethod().getAnnotation(TimeoutCiruitBreaker.class);
        long timeout = annotation.timeout();
        Future<Object> future = executorService.submit(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return null;
            }
        });
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
//            e.printStackTrace();
            future.cancel(true);
            return "timeout ... ";
        }
        return null;
    }


    @Around("@annotation(com.spring.cloud.demo.annotion.SemaphoreCircuitBreaker)")
    public Object advancedSayInSemaphore(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        SemaphoreCircuitBreaker annotation = signature.getMethod().getAnnotation(SemaphoreCircuitBreaker.class);
        Semaphore semaphore = semaphoreMap.get(signature.getMethod());
        if(semaphore == null){
            semaphore = new Semaphore(annotation.value());
            semaphoreMap.put(signature.getMethod(), semaphore);
        }
        try {
            if(semaphore.tryAcquire()){
                return joinPoint.proceed();
            }else{
                System.out.println("访问量太大，稍后再试");
                return "访问量太大，稍后再试";
            }
        }finally {
            semaphore.release();
        }
    }


    @Around("@annotation(com.spring.cloud.demo.annotion.LimiterCiruitBreaker)")
    public Object advancedSayInLimiter(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LimiterCiruitBreaker annotation = signature.getMethod().getAnnotation(LimiterCiruitBreaker.class);
        Limiter limiter = limiterMap.get(signature.getMethod());
        if(limiter == null){
            limiter = new Limiter(annotation.interval(),
                    new AtomicInteger(annotation.limit()),
                    annotation.limit());
            limiterMap.put(signature.getMethod(), limiter);
        }
        if(limiter.isAllowable()){
            return joinPoint.proceed();
        }else{
            System.out.println("访问量太大，稍后再试");
            return "访问量太大，稍后再试";
        }
    }
}

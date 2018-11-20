package com.spring.cloud.demo.controller;

import com.spring.cloud.demo.annotion.LimiterCiruitBreaker;
import com.spring.cloud.demo.annotion.SemaphoreCircuitBreaker;
import com.spring.cloud.demo.annotion.TimeoutCiruitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 原生自实现熔断
 *
 * @author zhangjj
 * @create 2018-11-14 17:37
 **/
@RestController
public class NativeController {

    private Logger logger = LoggerFactory.getLogger(NativeController.class);


    /**
     * FutureTask 实现熔断
     * @return
     */
    @GetMapping("/work")
    public String work() {

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {

                return invoke();
            }
        };
        FutureTask<String> futureTask = new FutureTask(callable);
        new Thread(futureTask).start();
        String result = null;
        try {
            result = futureTask.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
//            e.printStackTrace();
            futureTask.cancel(true);
            return "timeout";
        }

        return result;
    }

    /**
     * 线程池实现熔断
     * @return
     */
    @GetMapping("/work1")
    public String work1() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return invoke();
            }
        });
        String result = null;
        try {
            result = future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
//            e.printStackTrace();
            future.cancel(true);
            return "timeout";
        }
        return result;
    }

    /**
     * 超时熔断
     * @return
     * @throws InterruptedException
     */
    @TimeoutCiruitBreaker(timeout = 3000)
    @GetMapping("/work2")
    public String work2() throws InterruptedException {
        return invoke();
    }

    /**
     * 信号量 熔断
     * @return
     * @throws InterruptedException
     */
    @SemaphoreCircuitBreaker(2)
    @GetMapping("/work3")
    public String work3() throws InterruptedException {
        return invoke();
    }


    @LimiterCiruitBreaker(limit = 2, interval = 60000)
    @GetMapping("/work4")
    public String work4() throws InterruptedException {
        return invoke();
    }

    private String invoke() throws InterruptedException {
        // 模拟业务执行时间
        Random random = new Random();
        int i = random.nextInt(5000);
        logger.info("开始执行业务啦。。。，需要耗时 {}", i);
        Thread.sleep(i);
        logger.info("业务执行用时 : {}", i);
        return Thread.currentThread().getName();
    }
}

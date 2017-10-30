package com.javanewb.controller;

import com.javanewb.consts.DefaultValues;
import com.keruyun.portal.common.configuration.ApplicationContextHolder;
import com.keruyun.portal.common.http.HttpClientComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * <p>
 * Description: com.javanewb.controller
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: 客如云
 * </p>
 * date：2017/10/25
 *
 * @author Dean.Hwang
 */
@Api
@RestController
@Slf4j
public class TestController {

    @Autowired
    private ApplicationContextHolder holder;
    @Autowired
    private HttpClientComponent httpClientComponent;
    ExecutorService executorService = new ThreadPoolExecutor(300, 10000, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));
    ExecutorService batchService = new ThreadPoolExecutor(300, 10000, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));

    @ApiOperation(value = "请求同步测试", notes = "请求同步测试")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public void async(HttpServletRequest request, HttpServletResponse response) {
        Long startTime = System.currentTimeMillis();
        TestCall testCall = new TestCall();
        Future<Integer> future = executorService.submit(testCall);
        log.info(Thread.currentThread().getName());
        try {
            Integer result = future.get(1000, TimeUnit.MILLISECONDS);
            response.getOutputStream().print(future.get());
            System.out.println(" time: " + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            try {
                response.getOutputStream().print(DefaultValues.DEFAULT_VAL);
                System.out.println(" time: " + (System.currentTimeMillis() - startTime));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "请求同步测试", notes = "请求同步测试")
    @RequestMapping(value = "/batch", method = RequestMethod.GET)
    public void batchTest() {
        for (int i = 0; i < 2000; i++) {
            batchService.execute(new BatchTester(i));
        }
    }

    class BatchTester implements Runnable {
        private int no;

        public BatchTester(int no) {
            this.no = no;
        }

        @Override
        public void run() {

            ResponseEntity<String> result = httpClientComponent.makeParam("", "http://localhost:8080/async").get(String.class);
            System.out.println(result.getBody());
        }
    }

    class TestCall implements Callable<Integer> {
        private int sum;

        @Override
        public Integer call() throws Exception {
            Thread.sleep(100);

            for (int i = 0; i < 5000; i++) {
                sum = sum + i;
            }
            return sum;
        }
    }

}

package com.javanewb.controller;

import com.javanewb.consts.DefaultValues;
import com.javanewb.entity.TestThreadHolder;
import com.javanewb.thread.tools.RequestHolder;
import com.keruyun.portal.common.filter.LoggerMDCFilter;
import com.keruyun.portal.common.http.HttpClientComponent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private HttpClientComponent httpClientComponent;
    ExecutorService executorService = new ThreadPoolExecutor(300, 10000, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));
    ExecutorService batchService = new ThreadPoolExecutor(300, 10000, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));
    private RequestHolder<String> holder = new RequestHolder<>(100);
    private List<String> mdcList = new ArrayList<>();

    @ApiOperation(value = "请求同步测试", notes = "请求同步测试")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public void async(HttpServletRequest request, HttpServletResponse response) {
        Long startTime = System.currentTimeMillis();
        String mdc = MDC.get(LoggerMDCFilter.IDENTIFIER);
        mdcList.add(mdc);
        Future<String> future = holder.getFuture(mdc, TestThreadHolder.class);
        log.info(Thread.currentThread().getName());
        try {
            String result = future.get(4500, TimeUnit.SECONDS);
            response.getOutputStream().print(result);
            System.out.println(" time: " + (System.currentTimeMillis() - startTime));
        } catch (IOException | ExecutionException e) {
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

    @ApiOperation(value = "释放list第一个", notes = "请求同步测试")
    @RequestMapping(value = "/notify", method = RequestMethod.GET)
    public void notifyFirst() {
        String mdc = mdcList.get(0);
        mdcList.remove(0);
        holder.notifyThread(mdc, "");
    }

}

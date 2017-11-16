package com.javanewb.controller;

import com.javanewb.entity.TestThreadHolder;
import com.javanewb.thread.tools.RequestHolder;
import com.keruyun.portal.common.filter.LoggerMDCFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    private RequestHolder<String> holder = new RequestHolder<>(100, 500000L);
    private List<String> mdcList = new ArrayList<>();

    @ApiOperation(value = "请求同步测试", notes = "请求同步测试")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public void async(HttpServletRequest request, HttpServletResponse response, String id) {
        Long startTime = System.currentTimeMillis();
        String mdc = MDC.get(LoggerMDCFilter.IDENTIFIER);
        mdcList.add(mdc);
        Future<String> future = holder.getFuture(id, TestThreadHolder.class);
        log.info(Thread.currentThread().getName());
        try {
            System.out.println(mdc + " Thread Wait");
            String result = future.get();
            response.getOutputStream().print(result);
            System.out.println(" time: " + (System.currentTimeMillis() - startTime));
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "释放list第一个", notes = "请求同步测试")
    @RequestMapping(value = "/notify", method = RequestMethod.GET)
    public String notifyFirst() {
        String mdc = mdcList.get(0);
        mdcList.remove(0);
        holder.notifyThread(mdc, "");
        return mdc;
    }

    @ApiOperation(value = "释放list第一个", notes = "请求同步测试")
    @RequestMapping(value = "/notifyThis", method = RequestMethod.GET)
    public String notifyThis(String mdc) {
        int idx = 0;
        for (int i = 0; i < mdcList.size(); i++) {
            if (mdcList.get(i).equals(mdc)) {
                idx = i;
                break;
            }
        }
        if (mdcList.size() > 0) {
            mdcList.remove(idx);
        }
        holder.notifyThread(mdc, "");
        return mdc;
    }
}

package com.javanewb.controller;

import com.javanewb.common.filter.LoggerMDCFilter;
import com.javanewb.entity.TestThreadHolder;
import com.javanewb.thread.tools.RequestHolder;
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
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>
 * Description: com.javanewb.controller
 * </p>
 * <p>
 * date：2017/10/25
 *
 * @author Dean.Hwang
 */
@Api
@RestController
@Slf4j
public class TestController {
    private RequestHolder<String> holder = new RequestHolder<>(1000, 8000L);
    private List<String> mdcList = new CopyOnWriteArrayList<>();
    private Random random = new Random();

    @ApiOperation(value = "请求同步测试", notes = "请求同步测试")
    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public void async(HttpServletRequest request, HttpServletResponse response, String id) {
        String mdc = MDC.get(LoggerMDCFilter.IDENTIFIER);
        mdcList.add(id);
        Future<String> future = holder.getFuture(id, TestThreadHolder.class);
        log.info(Thread.currentThread().getName());
        try {
            String result = future.get();
            response.getOutputStream().print(result);
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "释放list第一个", notes = "请求同步测试")
    @RequestMapping(value = "/notify", method = RequestMethod.GET)
    public String notifyRandom() {
        String mdc = getMdc();
        if (mdc == null) {
            return "{\"result\":\"empty pool\"}";

        }
        holder.notifyThread(mdc, "");
        return "{\"result\":\"" + mdc + "\"}";
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

    private synchronized String getMdc() {
        if (mdcList.size() == 0) {
            return null;
        }
        Integer idx = random.nextInt(mdcList.size());
        String mdc = mdcList.get(idx);
        mdcList.remove(mdc);
        return mdc;
    }
}

package com.javanewb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 * Description: com.javanewb.entity
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: 客如云
 * </p>
 * date：2017/10/26
 *
 * @author Dean.Hwang
 */
public class RequestHolder {
    private Long holdTime = 1000L;
    private Integer threshold = 100;
    private List<ThreadData> threadList = new CopyOnWriteArrayList<>();
    private volatile boolean sendFlag = false;

    @Data
    @AllArgsConstructor
    class ThreadData {
        private String mdc;
        private String defaultValue;
        private HttpServletResponse response;
        private Thread thread;

    }

    public RequestHolder(Long holdTime, Integer threshold) {
        this.holdTime = holdTime;
        this.threshold = threshold;
    }

    public void put(String mdc, String defaultValue, HttpServletResponse res, Thread thread) {
        try {
            thread.wait(holdTime);
        } catch (InterruptedException e) {
            try {
                res.getOutputStream().print(defaultValue);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        threadList.add(new ThreadData(mdc, defaultValue, res, thread));
        if (sendFlag && threadList.size() >= threshold) {
            sendIdle();
        }
    }

    private void sendIdle() {
        //TODO send over threadData
    }
}

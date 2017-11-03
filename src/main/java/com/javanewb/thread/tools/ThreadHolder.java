package com.javanewb.thread.tools;

import lombok.Data;

import java.util.concurrent.Callable;

/**
 * <p>
 * Description: com.javanewb.service
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: 客如云
 * </p>
 * date：2017/10/31
 *
 * @author Dean.Hwang
 */
@Data
public abstract class ThreadHolder<T> implements Callable<T> {
    protected abstract T proData();//TODO 正常逻辑处理,以及默认数据返回

    private T defaultData;//返回的默认数据
    private Object needProData;//接受到需要处理的数据
    private Long createTime = System.currentTimeMillis();

    @Override
    public T call() throws Exception {
        waitThread();
        return proData();
    }

    public synchronized void waitThread() throws InterruptedException {
        this.wait();
    }

    public synchronized void notifyThread(Object needProData) {
        this.needProData = needProData;
        this.notify();
    }

    public synchronized void notifyDefault() {
        this.notify();
    }
}

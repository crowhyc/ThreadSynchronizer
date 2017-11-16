package com.javanewb.thread.tools;

import com.keruyun.portal.common.exception.BusinessException;
import com.keruyun.portal.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
@Slf4j
public class RequestHolder<T> {
    private Integer maxSize;
    private Long waitTime;

    public RequestHolder(Integer maxSize, Long maxWait, ExecutorService executorService) {
        if (maxSize > 1000) {
            throw new BusinessException(1022, "Bigger than max size num");
        }
        this.maxSize = maxSize;
        this.waitTime = maxWait;
        if (executorService != null) {
            this.executorService = executorService;
        } else {
            this.executorService = new ThreadPoolExecutor(Math.max(1, maxSize / 5), maxSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(maxSize));
        }
    }

    public RequestHolder(Integer maxSize, Long maxWait) {
        if (maxSize > 1000) {
            throw new BusinessException(1022, "Bigger than  max size num");
        }
        this.waitTime = maxWait;
        this.maxSize = maxSize;
        this.executorService = new ThreadPoolExecutor(Math.max(1, maxSize / 5), maxSize, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(maxSize));
    }

    private ExecutorService executorService;
    private final Map<String, ThreadHolder<T>> holderMap = new ConcurrentHashMap<>();
    private List<String> mdcOrderList = new CopyOnWriteArrayList<>();
    private AtomicBoolean isCleaning = new AtomicBoolean(false);

    public ThreadHolder<T> removeThread(String mdc, boolean needNotifyDefault) {
        mdcOrderList.remove(mdc);
        ThreadHolder<T> holder;
        synchronized (holderMap) {
            holder = holderMap.get(mdc);
            holderMap.remove(mdc);
        }
        if (holder != null && needNotifyDefault) {
            holder.notifyDefault();
        }
        return holder;
    }

    public void notifyThread(String mdc, Object data) {
        ThreadHolder<T> holder = removeThread(mdc, false);
        if (holder != null) {
            holder.notifyThread(data);
            log.info("Block pool has thread num:{} ", mdcOrderList.size());
        }
    }


    public Future<T> getFuture(String mdcStr, Class<? extends ThreadHolder<T>> holder) {
        if (StringUtil.isEmpty(mdcStr) || holder == null) {
            throw new BusinessException(1020, "Mdc target missing!!!");
        }
        Future<T> future;
        try {
            ThreadHolder<T> thread = holder.newInstance();
            holderMap.put(mdcStr, thread);
            mdcOrderList.add(mdcStr);
            thread.setMaxWaitTime(waitTime);
            thread.setMdc(mdcStr);
            thread.setHolder(this);
            future = executorService.submit(thread);
            cleanThreadPool();
        } catch (InstantiationException | IllegalAccessException e) {
            holderMap.remove(mdcStr);
            mdcOrderList.remove(mdcStr);
            throw new BusinessException(1021, "Thread Holder initialized failed");
        }
        return future;
    }

    private void cleanThreadPool() {
        if (mdcOrderList.size() >= maxSize && !isCleaning.get()) {
            isCleaning.set(true);
            try {
                mdcOrderList.subList(0, mdcOrderList.size() - maxSize).forEach(//看测试效率,看是否用并行stream处理
                        mdc -> removeThread(mdc, true)
                );
            } finally {
                isCleaning.set(false);
            }
        }
    }
}




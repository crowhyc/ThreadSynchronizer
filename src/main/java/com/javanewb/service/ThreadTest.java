package com.javanewb.service;

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
 * date：2017/10/26
 *
 * @author Dean.Hwang
 */
public class ThreadTest {
    static class TThread extends Thread {
        private Long startTime = System.currentTimeMillis();

        @Override
        public synchronized void run() {
            while (true) {
                System.out.println("im thread " + (System.currentTimeMillis() - startTime) / 1000);
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public synchronized void notifyThread() {
            notify();
        }

        public synchronized void waitThread(Long mills) throws InterruptedException {
            wait(mills);

        }
    }

    public static void main(String[] args) throws InterruptedException {
        TThread tThread = new TThread();
        tThread.start();
        Thread.sleep(3000);
        System.out.println("got lock");
        tThread.waitThread(10000L);
        Thread.sleep(1000);
        System.out.println("notify");
        tThread.notifyThread();
    }
}

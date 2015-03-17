package com.ctrip.infosec.flowtable4j.service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhangsx on 2015/3/13.
 */
public class Main {
    public static void main(String[] args) {
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        final HashMap<String, String> map = new HashMap<String, String>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        readLock.lock();
                        System.out.println(Thread.currentThread().getName() + ":" + map.get("1"));

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        readLock.unlock();
                    }
                }
            });
        }


        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    writeLock.lock();
                    map.put("1", new Date().toString());
                    writeLock.unlock();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    static class Inner{
        public static void main(String[] args) {

        }
    }
}

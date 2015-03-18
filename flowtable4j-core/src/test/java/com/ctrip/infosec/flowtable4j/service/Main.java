package com.ctrip.infosec.flowtable4j.service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhangsx on 2015/3/13.
 */
public class Main {
    public static void main(String[] args) {

        List<Integer> src =new ArrayList<Integer>();
        List<Integer> tgt =new ArrayList<Integer>();
        src.add(1);
        src.add(2);
        src.add(3);
        tgt.add(1);
        tgt.add(4);
        src.addAll(tgt);
        System.out.println(src.size());
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        final HashMap<Integer, String> map = new HashMap<Integer, String>();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            readLock.lock();
                            System.out.println(Thread.currentThread().getName());
                            for(Integer s: map.keySet()) {
                                System.out.println(map.get(s));
                            }
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            readLock.unlock();
                        }
                    }
                }
            });
        }

        final Random random= new Random();
        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        writeLock.lock();
                        int key=random.nextInt(10);
                        if(map.containsKey(key)){
                            map.remove(key);
                        } else {
                        map.put(key, new Date().toString());
                        }
                    }
                    catch (Exception ex) {
                        //
                    }
                    finally {
                        writeLock.unlock();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    static class Inner {
        public static void main(String[] args) {

        }
    }
}

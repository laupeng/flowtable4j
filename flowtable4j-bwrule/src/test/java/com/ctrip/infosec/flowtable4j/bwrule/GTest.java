package com.ctrip.infosec.flowtable4j.bwrule;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by thyang on 2015/3/24 0024.
 */
public class GTest {
    private ArrayBlockingQueue<Integer> concurrentLinkedQueue=new ArrayBlockingQueue<Integer>(100);
    private Lock lock=new ReentrantLock();
    private boolean stop=false;
    @Test
    @Ignore
    public void testConcurrentQ() throws InterruptedException, IOException {
        ThreadPoolExecutor executor=new ThreadPoolExecutor(64, 512, 60, TimeUnit.SECONDS, new SynchronousQueue(), new ThreadPoolExecutor.CallerRunsPolicy());
        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                for(int i=0;i<300;) {
                   concurrentLinkedQueue.put(i++);
                    Thread.sleep(1);
                }
                return null;
            }
        });


        System.out.println("Starting.....");
        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                 List<Integer> allInts=new ArrayList<Integer>();
                 for(;;) {
                     try {
                         int i = 0;
                         lock.lock();
                         while (i < 50) {
                             Integer e = concurrentLinkedQueue.poll();
                             if (e == null) {
                                 break;
                             } else {
                                 i++;
                                 allInts.add(e);
                             }
                         }
                     } finally {
                         lock.unlock();
                     }
                     if (allInts.size() > 0) {
                         for (Object s : allInts) {
                             System.out.println(s.toString() + " ID:" + String.valueOf(Thread.currentThread().getId()));
                         }
                         allInts.clear();
                     }
                     Thread.sleep(5);
                     if(stop){
                         break;
                     }
                 }
                return null;
            }
        });

        tasks.add(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                List<Integer> allInts=new ArrayList<Integer>();
                for(;;) {
                    try {
                        int i = 0;
                        lock.lock();
                        while (i < 50) {
                            Integer e = concurrentLinkedQueue.poll();
                            if (e == null) {
                                break;
                            } else {
                                i++;
                                allInts.add(e);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                    if (allInts.size() > 0) {
                        for (Object s : allInts) {
                            System.out.println(s.toString() + " ID:" + String.valueOf(Thread.currentThread().getId()));
                        }
                        allInts.clear();
                    }
                    Thread.sleep(5);
                    if(stop){
                        break;
                    }
                }
                return null;
            }
        });

        executor.invokeAll(tasks, 30, TimeUnit.SECONDS);



//        int i=0;
//        for(;;) {
//            if (concurrentLinkedQueue.size() < 200) {
//                concurrentLinkedQueue.add(i++);
//            }
//            Thread.sleep(2);
//        }
    }
}

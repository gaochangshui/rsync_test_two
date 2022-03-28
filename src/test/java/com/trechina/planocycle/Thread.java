package com.trechina.planocycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Thread {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30000; i++) {
            list.add("hello"+i);
        }

        exec(list);
    }
    public static void exec(List<String> list) throws InterruptedException {
        int count = 300;
        int listSize = list.size();
        int runSize = (listSize/count)+1;
        List<String> newList = null;
        ExecutorService executor = Executors.newFixedThreadPool(runSize);
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(runSize);
        //循环线程
        for (int i = 0; i < runSize; i++) {
            if ((i+1)==runSize){
                int startIndex = i*count;
                int endIndex = list.size();
                newList=list.subList(startIndex,endIndex);

            }else {
                int startIndex = i*count;
                int endIndex = (i+1)*count;
               newList = list.subList(startIndex,endIndex);
            }

            MyThread myThread = new MyThread(newList, begin, end);

            executor.execute(myThread);

        }
        begin.countDown();
        end.await();
        executor.shutdown();
    }




}
    class MyThread implements Runnable{
    private List<String> list;
    private CountDownLatch begin;
    private CountDownLatch end;
    private static Logger logger = LoggerFactory.getLogger(Thread.class);

    public MyThread(List<String> list,CountDownLatch begin,CountDownLatch end){
        this.list = list;
        this.begin = begin;
        this.end = end;

    }
        @Override
        public void run() {

            try {
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("刘鑫宇"+i);

                }
                begin.await();
            } catch (InterruptedException e) {
                logger.error("", e);
            }finally {
               end.countDown();
            }

        }
    }
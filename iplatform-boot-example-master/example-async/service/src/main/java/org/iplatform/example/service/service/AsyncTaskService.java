package org.iplatform.example.service.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

@Service
public class AsyncTaskService {

    @Async
    public void executeAsyncTask(Integer i) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("执行异步任务:" + i);
    }

    @Async
    public Future<String> executeAsyncFutureTask(Integer i) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String start = df.format(new Date());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String end = df.format(new Date());
        String message = String.format("start:%s,end:%s",start,end);
        return new AsyncResult<>(message);
    }
}
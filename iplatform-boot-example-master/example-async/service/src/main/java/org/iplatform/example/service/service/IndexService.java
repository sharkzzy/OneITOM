package org.iplatform.example.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author zhanglei
 */
@Configuration
@Service
@RestController
@RequestMapping("/api/v1")
public class IndexService {
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    AsyncTaskService asyncTaskService;

    @RequestMapping(value = "/noasync", method = RequestMethod.GET)
    public @ResponseBody
    String noasync() {
        Long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            //费spring实例化的对象无法使用@Async，所以此处不是异步
            AsyncTaskService service = new AsyncTaskService();
            service.executeAsyncTask(i);
        }
        Long e = System.currentTimeMillis();
        return String.format("ok cost %d", (e - s));
    }

    @RequestMapping(value = "/async", method = RequestMethod.GET)
    public @ResponseBody
    String async() {
        Long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            asyncTaskService.executeAsyncTask(i);
        }
        Long e = System.currentTimeMillis();
        return String.format("ok cost %d", (e - s));
    }

    @RequestMapping(value = "/asyncback", method = RequestMethod.GET)
    public @ResponseBody
    String asyncback() {
        Long s = System.currentTimeMillis();
        Set<Future> futures = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            futures.add(asyncTaskService.executeAsyncFutureTask(i));
        }

        while (true) {
            if(futures.isEmpty()){
                break;
            }else{
                Iterator<Future> it = futures.iterator();
                while (it.hasNext()) {
                    Future future = it.next();
                    if (future.isDone()) {
                        it.remove();
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Long e = System.currentTimeMillis();
        return String.format("ok cost %d", (e - s));
    }
}

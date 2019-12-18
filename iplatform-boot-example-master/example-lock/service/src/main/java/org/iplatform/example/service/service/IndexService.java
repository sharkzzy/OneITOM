package org.iplatform.example.service.service;

import org.iplatform.microservices.core.scheduled.lock.LockService;
import org.iplatform.microservices.core.scheduled.lock.core.LockModel;
import org.iplatform.microservices.core.scheduled.lock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@Service
@RestController
@RequestMapping("/api/v1")
public class IndexService {
	
    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);
    
    @Autowired(required=false)
    LockService lockService;
    
    @Scheduled(fixedDelay = 30*1000L)
    @SchedulerLock(model=LockModel.iammaster)
    public void schedule1(){
    	LOG.info("master模式--这个定时任务只能我执行，除非进程退出");
    }
    
    @Scheduled(fixedDelay = 30*1000L)
    @SchedulerLock(name="votemasterLock")
    public void schedule2(){
    	LOG.info("排他锁--我在执行的时候其他进程相同定时任务不能执行，直到我本次执行结束");
    }
    
    @Scheduled(fixedDelay = 30*1000L)
    public void schedule3(){
    	if(lockService != null && lockService.isMaser()){
    		LOG.info("当前节点是主节点，开始执行----------");
    	}
    }

    
}

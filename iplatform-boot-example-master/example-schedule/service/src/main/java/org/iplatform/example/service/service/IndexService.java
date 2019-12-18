package org.iplatform.example.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleCron(){
    	LOG.info("scheduleCron----有状态----上一次执行完成才会继续下一次");
    }
    
    @Scheduled(fixedRate = 60*1000L)
    public void scheduleFixedRate(){
    	LOG.info("scheduleFixedRate----无状态----不论上次是否执行完成，到触发时间就会执行下一次");
    }
    
    @Scheduled(fixedDelay = 60*1000L)
    public void scheduleFixedDelay(){
    	LOG.info("scheduleFixedDelay----有状态----上一次执行完成才会继续下一次");
    }
    
}

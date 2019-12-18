package org.iplatform.example.service.test;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.iplatform.example.service.custom.MericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 模拟数据定时变化
 */
@Service
public class ScheduledTaskService {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Autowired
	MericService mericService;
	
	@Scheduled(fixedRate = 5000)
	public void fixedRate(){
		mericService.getCustom().setTime(dateFormat.format(new Date()));	
	}		
}

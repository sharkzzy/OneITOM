package org.iplatform.example.service.custom;

import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 自定义查询
 * http://localhost:8081/myprojectservice/custom
 */
public class MetricEndPoint extends AbstractEndpoint<MetricBean> implements ApplicationContextAware{

	ApplicationContext context;
	
	public MetricEndPoint() {
		super("custom");
	}

	@Override
	public MetricBean invoke() {
		//重写invoke方法定义返回要监控的内容
		MericService statusService = context.getBean(MericService.class);
		return statusService.getCustom();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

}

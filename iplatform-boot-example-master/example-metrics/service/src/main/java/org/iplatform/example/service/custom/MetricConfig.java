package org.iplatform.example.service.custom;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {
	
	/**
	 * 注册端点
	 * @return
	 */
	@Bean
	public Endpoint<MetricBean> custom(){
		Endpoint<MetricBean> custom = new MetricEndPoint();
		return custom;
	}
}

package org.iplatform.example.service.custom;

import org.springframework.stereotype.Service;

/**
 * 注册一个监控对象的服务类，通过这个服务修改监控点对象数据
 * @author zhanglei
 *
 */
@Service
public class MericService {
	private MetricBean custom = new MetricBean();

	public MetricBean getCustom() {
		return custom;
	}

	public void setCustom(MetricBean custom) {
		this.custom = custom;
	}

}

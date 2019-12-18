package org.iplatform.example.service.custom;

/**
 * 定义监控点数据结构体，返回时会转换成json格式
 * @author zhanglei
 *
 */
public class MetricBean {
	private String name="zhanglei";
	private String time;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}

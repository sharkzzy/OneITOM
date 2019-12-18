package org.iplatform.example.service.entity;

import java.io.Serializable;

public class OrderEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long order_id;
	private Long user_id;

	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

}
package org.iplatform.example.service.entity;

import java.io.Serializable;

public class OrderItemEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long order_item_id;
	private Long user_id;
	private String status;

	public Long getOrder_item_id() {
		return order_item_id;
	}

	public void setOrder_item_id(Long order_item_id) {
		this.order_item_id = order_item_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
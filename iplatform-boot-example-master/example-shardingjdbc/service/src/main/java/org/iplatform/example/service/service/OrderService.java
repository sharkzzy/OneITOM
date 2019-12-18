package org.iplatform.example.service.service;

import java.util.List;
import java.util.Map;

import org.iplatform.example.service.entity.OrderEntity;
import org.iplatform.example.service.entity.OrderItemEntity;
import org.iplatform.example.service.mapper.OrderMapper;
import org.iplatform.microservices.core.db.dynamic.TargetDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/v1/order")
public class OrderService {

	@Autowired
	OrderMapper orderMapper;

	@TargetDataSource(name="shardingds")
	@RequestMapping(value = "/getOrders", method = RequestMethod.GET)
	public List<OrderEntity> getOrders() {
		List<OrderEntity> orders = null;
		try {
			orders = orderMapper.getOrders();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orders;
	}
	
	@TargetDataSource(name="shardingds")
	@RequestMapping(value = "/getOrderItems", method = RequestMethod.GET)
	public List<OrderItemEntity> getOrderItems() {
		List<OrderItemEntity> orderItems = null;
		try {
			orderItems = orderMapper.getOrderItems();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderItems;
	}
	
	@TargetDataSource(name="shardingds")
	@RequestMapping(value = "/getRelatedDatas", method = RequestMethod.GET)
	public List<Map<String,Object>> getRelatedDatas() {
		List<Map<String,Object>> relatedDatas = null;
		try {
			relatedDatas = orderMapper.getRelatedDatas();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return relatedDatas;
	}
	
	@TargetDataSource(name="shardingds")
	@RequestMapping(value = "/addOrder", method = RequestMethod.PUT)
	public OrderEntity addOrder(@RequestBody OrderEntity order) {
		OrderEntity orderEntity = null;
		try {
			int count = orderMapper.insertOrder(order);
			if (count > 0) {
				orderEntity = order;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderEntity;
	}
	
	@TargetDataSource(name="shardingds")
	@RequestMapping(value = "/addOrderItem", method = RequestMethod.PUT)
	public OrderItemEntity addOrderItem(@RequestBody OrderItemEntity orderItem) {
		OrderItemEntity orderItemEntity = null;
		try {
			int count = orderMapper.insertOrderItem(orderItem);
			if (count > 0) {
				orderItemEntity = orderItem;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderItemEntity;
	}

}

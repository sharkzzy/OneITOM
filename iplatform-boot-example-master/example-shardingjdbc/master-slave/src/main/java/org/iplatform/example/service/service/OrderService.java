package org.iplatform.example.service.service;

import java.util.List;

import org.iplatform.example.service.entity.OrderEntity;
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

}

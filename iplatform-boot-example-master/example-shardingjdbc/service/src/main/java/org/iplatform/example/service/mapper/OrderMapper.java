package org.iplatform.example.service.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.iplatform.example.service.entity.OrderEntity;
import org.iplatform.example.service.entity.OrderItemEntity;

@Mapper
public interface OrderMapper {
	
	@Select("<script>SELECT * FROM t_order</script>")
	List<OrderEntity> getOrders() throws SQLException;
	
	@Select("<script>SELECT * FROM t_order_item</script>")
	List<OrderItemEntity> getOrderItems() throws SQLException;
	
	@Select("<script>SELECT o.user_id o_user_id,o.status o_status,i.user_id i_user_id,i.status i_status FROM t_order o,t_order_item i WHERE o.order_id = i.order_item_id</script>")
	List<Map<String,Object>> getRelatedDatas() throws SQLException;
	
	@Insert("<script>INSERT INTO t_order (user_id,status) "
			+ "VALUES (#{user_id,jdbcType = NUMERIC},#{status,jdbcType = VARCHAR})</script>")
	int insertOrder(OrderEntity order) throws SQLException;
	
	@Insert("<script>INSERT INTO t_order_item (order_item_id,user_id,status) "
			+ "VALUES (#{order_item_id,jdbcType = NUMERIC},#{user_id,jdbcType = NUMERIC},#{status,jdbcType = VARCHAR})</script>")
	int insertOrderItem(OrderItemEntity orderItem) throws SQLException;
	
}

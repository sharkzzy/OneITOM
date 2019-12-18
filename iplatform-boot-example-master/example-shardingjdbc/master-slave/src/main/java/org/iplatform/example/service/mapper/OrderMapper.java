package org.iplatform.example.service.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.iplatform.example.service.entity.OrderEntity;

@Mapper
public interface OrderMapper {
	
	@Select("<script>SELECT * FROM t_order</script>")
	List<OrderEntity> getOrders() throws SQLException;

	@Insert("<script>INSERT INTO t_order (order_id,user_id) "
			+ "VALUES (#{order_id,jdbcType = NUMERIC},#{user_id,jdbcType = NUMERIC})</script>")
	int insertOrder(OrderEntity order) throws SQLException;
	
}

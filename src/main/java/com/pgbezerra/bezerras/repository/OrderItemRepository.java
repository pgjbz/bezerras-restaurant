package com.pgbezerra.bezerras.repository;

import java.util.List;

import com.pgbezerra.bezerras.models.entity.Order;
import com.pgbezerra.bezerras.models.entity.OrderItem;

public interface OrderItemRepository extends Repository<OrderItem, Long> {
	List<OrderItem> findByOrder(Order order);
}

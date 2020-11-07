package com.pgbezerra.bezerras.repository;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderItem;

public interface OrderItemRepository extends Repository<OrderItem, Long> {
	List<OrderItem> findByOrder(Order order);
}

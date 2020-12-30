package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.Order;

import java.util.List;

public interface OrderService extends Service<Order, Long> {
    Boolean updateStatus(Order order);
    List<Order> findPendingOrders();
}

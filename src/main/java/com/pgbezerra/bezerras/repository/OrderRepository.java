package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.entities.model.Order;

import java.util.List;

public interface OrderRepository extends Repository<Order, Long> {
    List<Order> findPendingOrders();
}

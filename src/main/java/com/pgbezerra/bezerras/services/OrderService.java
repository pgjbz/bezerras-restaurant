package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.Order;

public interface OrderService extends Service<Order, Long> {
    Boolean updateStatus(Order order);
}

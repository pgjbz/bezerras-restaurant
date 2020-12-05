package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.Order;

import java.util.List;

public interface OrderService {
    Order insert(Order obj);
    Boolean update(Order obj);
    List<Order> findAll();
    Order findById(Long id);
    Boolean deleteById(Long id);
}

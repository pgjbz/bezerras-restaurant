package com.pgbezerra.bezerras.services;

import com.pgbezerra.bezerras.entities.model.OrderItem;

public interface OrderItemService {

    OrderItem insert(OrderItem obj);
    Boolean deleteById(Long id);

}

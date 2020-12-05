package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.services.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem insert(OrderItem obj) {
        return orderItemRepository.insert(obj);
    }

    @Override
    public Boolean deleteById(Long id) {
        return orderItemRepository.deleteById(id);
    }
}

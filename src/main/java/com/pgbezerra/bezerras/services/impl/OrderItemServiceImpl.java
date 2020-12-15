package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.repository.OrderItemRepository;
import com.pgbezerra.bezerras.services.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private OrderItemRepository orderItemRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem insert(OrderItem orderItem) {
        return orderItemRepository.insert(orderItem);
    }

    @Override
    public Boolean update(OrderItem orderItem) {
        return orderItemRepository.update(orderItem);
    }

    @Override
    public List<OrderItem> findAll() {
        throw new UnsupportedOperationException("Method Unsupported");
    }

    @Override
    public OrderItem findById(Long id) {
        throw new UnsupportedOperationException("Method Unsupported");
    }

    @Override
    public Boolean deleteById(Long id) {
        return orderItemRepository.deleteById(id);
    }
}

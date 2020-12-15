package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.entities.model.Order;
import com.pgbezerra.bezerras.entities.model.OrderItem;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.services.*;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class);
    private static final Integer DOING = 2;

    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final OrderAddressService orderAddressService;
    private final TableService tableService;

    public OrderServiceImpl(
            final OrderRepository orderRepository,
            final OrderItemService orderItemService,
            final ProductService productService,
            final OrderAddressService orderAddressService,
            final TableService tableService) {
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.productService = productService;
        this.orderAddressService = orderAddressService;
        this.tableService = tableService;
    }

    @Override
    public Order insert(Order order) {
        if(order.getItems().isEmpty())
            throw new ResourceBadRequestException("Order items not be empty");
        order.setId(null);
        order.setDate(new Date());
        if(Objects.nonNull(order.getTable())) {
            LOG.info(String.format("Finding table with id %s", order.getTable().getId()));
            order.setTable(tableService.findById(order.getTable().getId()));
        }
        order.setOrderStatus(DOING);
        LOG.info(String.format("Finding products of order %s", order.toString()));
        getItems(order);
        LOG.info(String.format("Calculating value of order %s", order.toString()));
        order.calcOrderValue();
        LOG.info(String.format("Inserting order %s", order.toString()));
        orderRepository.insert(order);

        if(order.getOrderType() == OrderType.DELIVERY) {
            if (Objects.nonNull(order.getOrderAddress()))
                orderAddressService.insert(order.getOrderAddress());
            else
                throw new ResourceBadRequestException("Order address in order type DELIVERY not be empty");
        }

        saveItems(order);

        return order;
    }

    @Override
    public Boolean update(Order order) {
        Order oldOrder = findById(order.getId());
        getItems(order);
        updateItems(order, oldOrder);
        oldOrder.getItems().removeAll(order.getItems());
        deleteItems(oldOrder);
        order.calcOrderValue();
        return orderRepository.update(order);
    }

    @Override
    public List<Order> findAll() {
        List<Order> orders = orderRepository.findAll();
        if(!orders.isEmpty())
            return orders;
        throw new ResourceNotFoundException("No orders found");
    }

    @Override
    public Order findById(Long id) {
        LOG.info(String.format("Finding order %s", id));
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("No order found with id %s", id)));
    }

    @Override
    public Boolean deleteById(Long id) {
        Order order = findById(id);
        LOG.info(String.format("Start deleting order %s", id));
        if(order.getOrderType() == OrderType.DELIVERY)
            orderAddressService.deleteById(order.getOrderAddress().getId());
        deleteItems(order);
        return orderRepository.deleteById(id);
    }

    private void saveItems(Order order){
        for (OrderItem orderItem : order.getItems()) {
            LOG.info(String.format("Inserting order item %s of order %s", orderItem.toString(), order.getId()));
            orderItemService.insert(orderItem);
        }
    }

    private void getItems(Order order) {
        for (OrderItem orderItem : order.getItems()) {
            LOG.info(String.format("Finding product %s", orderItem.getProduct().getId()));
            orderItem.setValue(productService.findById(orderItem.getProduct().getId()).getValue());
        }
    }

    private void deleteItems(Order order){
        for(OrderItem orderItem: order.getItems()) {
            LOG.info(String.format("Deleting order item %s", orderItem.toString()));
            orderItemService.deleteById(orderItem.getId());
        }
    }

    private void updateItems(Order order, Order oldOrder){
        for(OrderItem orderItem: order.getItems()) {
            LOG.info(String.format("Updating order item %s", orderItem.toString()));
            OrderItem oldOrderItem = oldOrder.getItems().stream().filter(oi -> oi.equals(orderItem)).findFirst().orElse(null);
            if(Objects.nonNull(oldOrderItem) && oldOrderItem.getQuantity().compareTo(orderItem.getQuantity()) != 0)
                orderItemService.update(orderItem);
        }
    }
}

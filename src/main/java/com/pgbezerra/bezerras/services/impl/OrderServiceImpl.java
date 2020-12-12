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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class);

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
    public Order insert(Order obj) {

        obj.setId(null);
        obj.setDate(new Date());
        if(Objects.nonNull(obj.getTable())) {
            LOG.info(String.format("Finding table with id %s", obj.getTable().getId()));
            obj.setTable(tableService.findById(obj.getTable().getId()));
        }
        obj.setOrderStatus(2);
        LOG.info(String.format("Finding products of order %s", obj.toString()));
        getItems(obj);
        LOG.info(String.format("Calculating value of order %s", obj.toString()));
        obj.setValue(calcOderValue(obj));
        LOG.info(String.format("Inserting order %s", obj.toString()));
        orderRepository.insert(obj);

        if(obj.getOrderType() == OrderType.DELIVERY) {
            if (Objects.nonNull(obj.getOrderAddress()))
                orderAddressService.insert(obj.getOrderAddress());
            else
                throw new ResourceBadRequestException("Order address in order type DELIVERY not be empty");
        }

        saveItems(obj);

        return obj;
    }

    @Override
    public Boolean update(Order obj) {
        Order oldObj = findById(obj.getId());
        getItems(obj);
        updateItems(obj, oldObj);
        oldObj.getItems().removeAll(obj.getItems());
        deleteItems(oldObj);
        obj.setValue(calcOderValue(obj));
        return orderRepository.update(obj);
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

    private void saveItems(Order obj){
        for (OrderItem orderItem : obj.getItems()) {
            LOG.info(String.format("Inserting order item %s of order %s", orderItem.toString(), obj.getId()));
            orderItemService.insert(orderItem);
        }
    }

    private void getItems(Order obj) {
        for (OrderItem orderItem : obj.getItems()) {
            LOG.info(String.format("Finding product %s", orderItem.getProduct().getId()));
            orderItem.setValue(productService.findById(orderItem.getProduct().getId()).getValue());
        }
    }

    private void deleteItems(Order obj){
        for(OrderItem orderItem: obj.getItems()) {
            LOG.info(String.format("Deleting order item %s", orderItem.toString()));
            orderItemService.deleteById(orderItem.getId());
        }
    }

    private BigDecimal calcOderValue(Order obj) {
        BigDecimal value = BigDecimal.ZERO;
        for (OrderItem orderItem : obj.getItems())
            value = value.add(orderItem.getValue().multiply(BigDecimal.valueOf(orderItem.getQuantity().longValue())));
        if (obj.getOrderType() == OrderType.DELIVERY) {
            LOG.info(String.format("Delivery order %s", obj.toString()));
            if(Objects.nonNull(obj.getDeliveryValue()) && obj.getDeliveryValue().intValue() > 0) {
                LOG.info(String.format("Delivery value %s", obj.getDeliveryValue()));
                value = value.add(obj.getDeliveryValue());
            } else {
                LOG.info("Default delivery value 5.0");
                BigDecimal defaultValue = BigDecimal.valueOf(5.0);
                obj.setDeliveryValue(defaultValue);
                value = value.add(defaultValue);
            }
        }
        LOG.info(String.format("Final value of order %s: %s", obj.toString(), value));
        return value;
    }

    private void updateItems(Order obj, Order oldObj){
        for(OrderItem orderItem: obj.getItems()) {
            LOG.info(String.format("Updating order item %s", orderItem.toString()));
            OrderItem oldOrderItem = oldObj.getItems().stream().filter(oi -> oi.equals(orderItem)).findFirst().orElse(null);
            if(Objects.nonNull(oldOrderItem) && oldOrderItem.getQuantity().compareTo(orderItem.getQuantity()) != 0)
                orderItemService.update(orderItem);
        }
    }
}

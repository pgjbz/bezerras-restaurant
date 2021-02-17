package com.pgbezerra.bezerras.services.impl;

import com.pgbezerra.bezerras.models.dto.ReportDTO;
import com.pgbezerra.bezerras.models.entity.Order;
import com.pgbezerra.bezerras.models.entity.OrderItem;
import com.pgbezerra.bezerras.models.entity.Product;
import com.pgbezerra.bezerras.models.enums.OrderType;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.services.*;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = Logger.getLogger(OrderServiceImpl.class);
    private static final Integer DOING = 1;

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
        if(order.getOrderType() != OrderType.TABLE)
            order.setTable(null);

        if(Objects.nonNull(order.getTable()) && order.getOrderType() == OrderType.TABLE) {
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
        List<OrderItem> bkpOrderItem = new ArrayList<>(oldOrder.getItems());
        getItems(order);
        oldOrder.getItems().retainAll(order.getItems());
        bkpOrderItem.removeAll(order.getItems());
        oldOrder.getItems().forEach(item -> {
            OrderItem oi = order.getItems().stream().filter(i -> i.getProduct().equals(item.getProduct())).findFirst().orElse(null);
            if(Objects.nonNull(oi))
                item.setQuantity(oi.getQuantity());
        });
        updateItems(oldOrder);
        deleteItems(bkpOrderItem);
        oldOrder.calcOrderValue();
        return orderRepository.update(oldOrder);
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
        deleteItems(order.getItems());
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
            Product product =  productService.findById(orderItem.getProduct().getId());
            orderItem.setProduct(product);
            orderItem.setValue(product.getValue());
        }
    }

    private void deleteItems(List<OrderItem> items){
        for(OrderItem orderItem: items) {
            LOG.info(String.format("Deleting order item %s", orderItem.toString()));
            orderItemService.deleteById(orderItem.getId());
        }
    }

    private void updateItems(Order oldOrder){
        for(OrderItem orderItem: oldOrder.getItems()) {
            OrderItem oldOrderItem = oldOrder.getItems().stream().filter(oi -> oi.equals(orderItem)).findFirst().orElse(null);
            LOG.info(String.format("Updating order item %s", oldOrderItem));
            if(Objects.nonNull(oldOrderItem))
                orderItemService.update(orderItem);
        }
    }

    @Override
    public Boolean updateStatus(Order order) {
        Order oldOrder = findById(order.getId());
        updateStatus(oldOrder, order);
        return orderRepository.update(oldOrder);
    }

    @Override
    public List<Order> findPendingOrders() {
        List<Order> orders = orderRepository.findPendingOrders();
        if(!orders.isEmpty())
            return orders;
        throw new ResourceNotFoundException("No orders found");
    }

    @Override
    public List<ReportDTO> findReport(Date initialDate, Date finalDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        LOG.info(String.format("Finding order reports between %s and %s", sdf.format(initialDate), sdf.format(finalDate)));
        List<ReportDTO> reports = orderRepository.report(initialDate, finalDate);
        if(!reports.isEmpty())
            return reports;
        throw new ResourceNotFoundException(String.format("No orders reports between %s and %s", sdf.format(initialDate), sdf.format(finalDate)));
    }

    private void updateStatus(Order oldOrder, Order order) {
        oldOrder.setOrderStatus(order.getOrderStatus().getStatusCode());
    }
}

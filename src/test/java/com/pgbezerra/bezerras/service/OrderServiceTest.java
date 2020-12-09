package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.entities.model.*;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.services.*;
import com.pgbezerra.bezerras.services.exception.BadRequestException;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.OrderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class OrderServiceTest {

    public OrderServiceTest() {
    }

    @TestConfiguration
    static class OrderServiceTestConfigurarion {
        @Bean
        public OrderService orderService(
                final OrderRepository orderRepository,
                final OrderItemService orderItemService,
                final ProductService productService,
                final OrderAddressService orderAddressService,
                final TableService tableService) {
            return new OrderServiceImpl(orderRepository,
                    orderItemService,
                    productService,
                    orderAddressService,
                    tableService);
        }
    }

    private Order o1;
    private Order o2;

    private OrderItem oi1;
    private Product p1;
    private OrderAddress oa1;
    private Table t1;

    @Before
    public void start() {
        o1 = new Order(null, null, null, null, null, null, null, null);
        o2 = new Order(1L, null, null, null, null, null, null, null);
        oi1 = new OrderItem(1L, p1, null, Byte.valueOf("1"), null);
        p1 = new Product(1, "Feijoada", BigDecimal.valueOf(25.0), null);
        oa1 = new OrderAddress(1L, "Client A", "Street A", "1", "District A", "City A", "State A");
        t1 = new Table(1,"Table 1");
    }

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private OrderItemService orderItemService;
    @MockBean
    private ProductService productService;
    @MockBean
    private OrderAddressService orderAddressService;
    @MockBean
    private TableService tableService;

    @Test
    public void insertOrderDeliveryWithoutDeliveryValueExpectedSuccess() {
        o1.setOrderType(1);
        o1.setOrderAddress(oa1);
        oi1.setProduct(p1);
        o1.getItems().add(oi1);

        Mockito.when(orderRepository.insert(o1)).thenReturn(o2);
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);

        o1 = orderService.insert(o1);
        Mockito.verify(orderRepository).insert(o1);
        Mockito.verify(productService).findById(p1.getId());
        Assert.isTrue(o1.getValue().compareTo(BigDecimal.ZERO) > 0, "Value not be 0");
    }

    @Test
    public void insertOrderDeliveryWithDeliveryValueExpectedSuccess() {
        o1.setOrderType(1);
        o1.setOrderAddress(oa1);
        o1.setDeliveryValue(BigDecimal.valueOf(10.0));
        oi1.setProduct(p1);
        o1.getItems().add(oi1);

        Mockito.when(orderRepository.insert(o1)).thenReturn(o2);
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);

        o1 = orderService.insert(o1);
        Mockito.verify(orderRepository).insert(o1);
        Mockito.verify(productService).findById(p1.getId());
        Assert.isTrue(o1.getValue().compareTo(BigDecimal.ZERO) > 0, "Value not be 0");
        Assert.isTrue(o1.getValue().doubleValue() == 35.0, "Final value not match, value has to be 35.0");
    }

    @Test(expected = BadRequestException.class)
    public void insertOrderDeliveryExpectedError(){
        o1.setOrderType(1);
        oi1.setProduct(p1);
        o1.getItems().add(oi1);

        Mockito.when(orderRepository.insert(o1)).thenReturn(o2);
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);

        o1 = orderService.insert(o1);
        Mockito.verify(orderRepository).insert(o1);
        Mockito.verify(productService).findById(p1.getId());
    }

    @Test
    public void insertSimpleOrderExpectedSuccess(){
        o1.setOrderType(2);
        o1.getItems().add(oi1);
        oi1.setProduct(p1);
        Mockito.when(orderRepository.insert(o1)).thenReturn(o2);
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);

        o1 = orderService.insert(o1);
        Mockito.verify(orderRepository).insert(o1);
        Mockito.verify(productService).findById(p1.getId());
        Assert.isTrue(o1.getValue().compareTo(BigDecimal.ZERO) > 0, "Value not be 0");

    }

    @Test(expected = ResourceNotFoundException.class)
    public void insertSimpleOrderExpectedSuccessError(){
        o1.setOrderType(2);
        oi1.setProduct(p1);
        o1.getItems().add(oi1);

        Mockito.when(orderRepository.insert(o1)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);

        o1 = orderService.insert(o1);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void updateNonExistentOrderExpectedError(){
        o1.setId(1L);
        Mockito.when(orderRepository.findById(o1.getId())).thenReturn(Optional.ofNullable(null));
        orderService.findById(oi1.getId());
    }

    @Test
    public void updateNonExistentOrderExpectedSuccess(){
        o1.setId(1L);
        o1.setOrderType(2);
        o2.setTable(t1);
        o2.setOrderType(2);
        Product p2 = new Product(2, "Feijoada Grande", BigDecimal.valueOf(35.0), null);
        OrderItem oi2 = new OrderItem(1L, p2, null, Byte.valueOf("1"), null);
        OrderItem oi3 = new OrderItem(1L, p2, null, Byte.valueOf("3"), null);
        o1.getItems().addAll(Arrays.asList(oi1, oi2)); //60.0
        o2.getItems().add(oi3); //70

        Mockito.when(orderRepository.findById(o2.getId())).thenReturn(Optional.ofNullable(o1));
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);
        Mockito.when(productService.findById(p2.getId())).thenReturn(p2);
        Mockito.when(orderItemService.update(Mockito.any())).thenReturn(Boolean.TRUE);

        orderService.update(o2);


        Assert.isTrue(o2.getValue().doubleValue() == 105.0, "Order value need to be 105.0");
        Mockito.verify(orderRepository).findById(o2.getId());
        Mockito.verify(productService).findById(p2.getId());
        Mockito.verify(productService, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(orderItemService).update(Mockito.any(OrderItem.class));
    }


}

package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.entities.dto.ReportDTO;
import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.model.*;
import com.pgbezerra.bezerras.repository.OrderRepository;
import com.pgbezerra.bezerras.services.*;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Test(expected = ResourceBadRequestException.class)
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
    public void updateExistentOrderExpectedSuccess(){
        o1.setId(1L);
        o1.setOrderType(2);
        o2.setTable(t1);
        o2.setOrderType(2);
        Product p2 = new Product(2, "Feijoada Grande", BigDecimal.valueOf(35.0), null);
        OrderItem oi2 = new OrderItem(1L, p2, null, Byte.valueOf("1"), BigDecimal.valueOf(35.0));
        OrderItem oi3 = new OrderItem(1L, p2, null, Byte.valueOf("3"), BigDecimal.valueOf(35.0));
        o1.getItems().addAll(Arrays.asList(oi1, oi2)); //60.0
        o2.getItems().add(oi3); //70

        Mockito.when(orderRepository.findById(o2.getId())).thenReturn(Optional.ofNullable(o1));
        Mockito.when(productService.findById(p1.getId())).thenReturn(p1);
        Mockito.when(productService.findById(p2.getId())).thenReturn(p2);
        Mockito.when(orderItemService.update(Mockito.any())).thenReturn(Boolean.TRUE);

        orderService.update(o2);
        o2.calcOrderValue();


        Assert.isTrue(o2.getValue().doubleValue() == 105.0, "Order value need to be 105.0");
        Mockito.verify(orderRepository).findById(o2.getId());
        Mockito.verify(productService).findById(p2.getId());
        Mockito.verify(productService, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(orderItemService).update(Mockito.any(OrderItem.class));
    }

    @Test
    public void updateOrderStatusExistentOrderExpectedSuccess(){
        o1.setId(1L);
        o1.setOrderType(2);
        o2.setTable(t1);
        o2.setOrderType(2);
        Product p2 = new Product(2, "Feijoada Grande", BigDecimal.valueOf(35.0), null);
        OrderItem oi2 = new OrderItem(1L, p2, null, Byte.valueOf("1"), null);
        o1.getItems().addAll(Arrays.asList(oi1, oi2));
        o2.getItems().addAll(o1.getItems());
        o2.setOrderStatus(OrderStatus.COMPLETE.getStatusCode());

        Mockito.when(orderRepository.findById(o2.getId())).thenReturn(Optional.ofNullable(o1));

        orderService.updateStatus(o2);

        Mockito.verify(orderRepository).findById(o2.getId());
    }

    @Test
    public void findPendingOrdersExpectedSuccess(){
        o1.setId(1L);
        o1.setOrderType(2);
        o2.setTable(t1);
        o2.setOrderType(2);
        Product p2 = new Product(2, "Feijoada Grande", BigDecimal.valueOf(35.0), null);
        OrderItem oi2 = new OrderItem(1L, p2, null, Byte.valueOf("1"), null);
        o1.getItems().addAll(Arrays.asList(oi1, oi2));
        o2.getItems().addAll(o1.getItems());

        List<Order> orders = new ArrayList<>(Arrays.asList(o1, o2));

        Mockito.when(orderRepository.findPendingOrders()).thenReturn(orders);

        List<Order> ordersReturn = orderService.findPendingOrders();

        Mockito.verify(orderRepository).findPendingOrders();
        Assert.isTrue(!ordersReturn.isEmpty(), "Orders not be empty");

    }

    @Test(expected = ResourceNotFoundException.class)
    public void findPendingOrdersExpectedException(){
        List<Order> ordersReturn = orderService.findPendingOrders();
    }

    @Test
    public void findOrdersReportExpectedSuccess() throws ParseException {
        Date initialDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
        Date finalDate = new Date();
        ReportDTO r1 = new ReportDTO();
        r1.setTotalOrders(20);
        r1.setAmount(BigDecimal.valueOf(1000.0));
        r1.setDate(initialDate);
        ReportDTO r2 = new ReportDTO();
        r2.setTotalOrders(15);
        r2.setAmount(BigDecimal.valueOf(750.0));
        r2.setDate(finalDate);
        Mockito.when(orderRepository.report(initialDate, finalDate)).thenReturn(Arrays.asList(r1, r2));
        List<ReportDTO> reports = orderService.findReport(initialDate, finalDate);
        Assert.isTrue(!reports.isEmpty(), "Reports not be empty");
        Mockito.verify(orderRepository).report(initialDate, finalDate);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void  findOrdersReportExpectedException() throws ParseException{
        Date initialDate = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
        Date finalDate = new Date();
        List<ReportDTO> reports = orderService.findReport(initialDate, finalDate);
    }

}

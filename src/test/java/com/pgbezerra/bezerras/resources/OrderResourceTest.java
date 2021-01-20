package com.pgbezerra.bezerras.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgbezerra.bezerras.entities.dto.OrderDTO;
import com.pgbezerra.bezerras.entities.dto.OrderItemDTO;
import com.pgbezerra.bezerras.entities.dto.OrderStatusDTO;
import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.entities.model.*;
import com.pgbezerra.bezerras.services.OrderService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderResource.class)
public class OrderResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO o1;
    private OrderDTO o2;
    private OrderDTO o3;
    private final List<Order> orders = new ArrayList<>();

    @Before
    public void setup() {
        OrderItemDTO oi1 = new OrderItemDTO();
        oi1.setProduct(1);
        oi1.setQuantity(Byte.parseByte("2"));
        OrderItemDTO oi2 = new OrderItemDTO();
        oi2.setProduct(2);
        oi2.setQuantity(Byte.parseByte("2"));
        o1 = new OrderDTO();
        o1.setClientName("Client 1");
        o1.setOrderType(OrderType.DELIVERY);
        o1.setCity("City A");
        o1.setStreet("Street A");
        o1.setState("State A");
        o1.setNumber("123");
        o1.setDistrict("District A");
        o1.getItems().addAll(Arrays.asList(oi1, oi2));
        o2 = new OrderDTO();
        o2.setClientName("Client 1");
        o2.setOrderType(OrderType.TABLE);
        o2.setTable(1);
        o2.getItems().add(oi1);
        o3 = new OrderDTO();
        o3.setClientName("Client 1");
        o3.setOrderType(OrderType.DESK);
        o3.getItems().addAll(Arrays.asList(oi1, oi2));
        orders.addAll(Arrays.asList(convertToEntity(o1), convertToEntity(o2), convertToEntity(o3)));
    }

    private Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        Table table = new Table(orderDTO.getTable(), null);
        order.setOrderType(orderDTO.getOrderType().getOrderTypeCode());
        order.setTable(table);
        order.getItems().addAll(orderDTO.getItems().stream().map(orderItem -> convertOrderItemToEntity(orderItem, order)).collect(Collectors.toList()));
        order.setOrderAddress(convertOrderAddressToEntity(orderDTO));
        return order;
    }

    private OrderItem convertOrderItemToEntity(OrderItemDTO orderItemDTO, Order order) {
        OrderItem orderItem = new OrderItem();
        Product product = new Product();
        product.setId(orderItemDTO.getProduct());
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemDTO.getQuantity());
        return orderItem;
    }

    private OrderAddress convertOrderAddressToEntity(OrderDTO orderDTO) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setCity(orderAddress.getCity());
        orderAddress.setClientName(orderDTO.getClientName());
        orderAddress.setComplement(orderDTO.getComplement());
        orderAddress.setNumber(orderDTO.getNumber());
        orderAddress.setState(orderAddress.getState());
        orderAddress.setDistrict(orderDTO.getDistrict());
        orderAddress.setStreet(orderDTO.getStreet());
        return orderAddress;
    }

    @Test
    public void insertNewOrderDeliveryExpectedSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderService.insert(Mockito.any(Order.class))).thenReturn(order);
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void insertNewOrderOrderExpectedSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderService.insert(Mockito.any(Order.class))).thenReturn(order);
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o2)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void insertNewOrderDeskExpectedSuccess() throws Exception {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderService.insert(Mockito.any(Order.class))).thenReturn(order);
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o3)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void insertNewOrderDeliveryExpectedError() throws Exception {
        o1.setStreet(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o1)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void findOrderByIdExpectedNotFound() throws Exception {
        Mockito.when(orderService.findById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(orderService).findById(1L);
    }

    @Test
    public void findOrderByIdExpectedOk() throws Exception {
        Mockito.when(orderService.findById(1L)).thenReturn(convertToEntity(o1));
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(convertToEntity(o1))));
        Mockito.verify(orderService).findById(1L);
    }

    @Test
    public void findAllOrdersExpectedNotFound() throws Exception {
        Mockito.when(orderService.findAll()).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(orderService).findAll();
    }

    @Test
    public void findAllOrdersExpectedOk() throws Exception {
        Mockito.when(orderService.findAll()).thenReturn(orders);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(orders)));
    }

    @Test
    public void editOrderExpectedBadRequest() throws Exception {
        Mockito.when(orderService.update(Mockito.any(Order.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void editOrderExpectedNotFound() throws Exception {
        Mockito.when(orderService.update(Mockito.any(Order.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o1)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void editOrderExpectedOk() throws Exception {
        Mockito.when(orderService.update(Mockito.any(Order.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(o2)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(orderService).update(Mockito.any(Order.class));
    }

    @Test
    public void editStatusExpectedOk() throws Exception {
        Mockito.when(orderService.updateStatus(Mockito.any(Order.class))).thenReturn(true);
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        orderStatusDTO.setOrderStatus(OrderStatus.COMPLETE);
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/status/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderStatusDTO)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(orderService).updateStatus(Mockito.any(Order.class));
    }

    @Test
    public void editStatusExpectedBadRequest() throws Exception {
        OrderStatusDTO orderStatusDTO = new OrderStatusDTO();
        mockMvc.perform(MockMvcRequestBuilders.put("/orders/status/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderStatusDTO)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void deleteOrderExpectedResourceNotFound() throws Exception {
        Mockito.when(orderService.deleteById(1L)).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(orderService).deleteById(1L);
    }

    @Test
    public void deleteOrderExpectedResourceNoContent() throws Exception {
        Mockito.when(orderService.deleteById(1L)).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/orders/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        Mockito.verify(orderService).deleteById(1L);
    }

    @Test
    public void findOrderReportExpectedSuccess() throws Exception {
        Mockito.when(orderService.findReport(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/report?initial=2020-01-01&final=2020-01-19"))
                .andExpect(MockMvcResultMatchers.status().isOk());
//        Mockito.verify(orderService).findReport(Mockito.any(Date.class), Mockito.any(Date.class));
    }

    @Test
    public void findOrderReportExpectedNotFound() throws Exception {
        Mockito.when(orderService.findReport(Mockito.any(Date.class), Mockito.any(Date.class))).thenThrow(ResourceNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.get("/orders/report?initial=2020-01-01&final=2020-01-19"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(orderService).findReport(Mockito.any(Date.class), Mockito.any(Date.class));
    }

}

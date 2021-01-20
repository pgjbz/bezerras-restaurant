package com.pgbezerra.bezerras.resources;

import com.pgbezerra.bezerras.entities.dto.OrderDTO;
import com.pgbezerra.bezerras.entities.dto.OrderItemDTO;
import com.pgbezerra.bezerras.entities.dto.OrderStatusDTO;
import com.pgbezerra.bezerras.entities.dto.ReportDTO;
import com.pgbezerra.bezerras.entities.model.*;
import com.pgbezerra.bezerras.services.OrderService;
import com.pgbezerra.bezerras.services.exception.ResourceBadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/orders")
public class OrderResource {


    private OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping(value = "/pending")
    public ResponseEntity<List<Order>> findPendingOrders() {
        return ResponseEntity.ok(orderService.findPendingOrders());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Order> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping(value = "/report")
    public ResponseEntity<List<ReportDTO>> findReport(@RequestParam(value = "initial") String initial,
                                                      @RequestParam(value = "final", required = false) String finalDate) {
        Date initialDate;
        Date fDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            initialDate = sdf.parse(initial);
            if(StringUtils.hasLength(finalDate))
                fDate = sdf.parse(finalDate);
        } catch (Exception e){
            throw new ResourceBadRequestException(String.format("Invalid date format %s correct format: yyyy-dd-mm", initial));
        }
        return ResponseEntity.ok(orderService.findReport(initialDate, Objects.nonNull(fDate) ? fDate : new Date()));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);
        order.setId(id);
        System.out.println(order.getItems().size());
        orderService.update(order);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @RequestBody @Valid OrderStatusDTO orderStatus) {
        Order order = new Order();
        order.setId(id);
        order.setOrderStatus(orderStatus.getOrderStatus().getStatusCode());
        orderService.updateStatus(order);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);
        order = orderService.insert(order);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
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
}

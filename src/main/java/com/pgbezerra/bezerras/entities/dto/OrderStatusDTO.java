package com.pgbezerra.bezerras.entities.dto;

import com.pgbezerra.bezerras.entities.enums.OrderStatus;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class OrderStatusDTO implements Serializable {

    private static final long serialVersionUID = -6045792564081836056L;

    @NotNull(message = "Order status not be empty or null")
    private OrderStatus orderStatus;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}

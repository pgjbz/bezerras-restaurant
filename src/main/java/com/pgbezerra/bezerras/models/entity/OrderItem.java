package com.pgbezerra.bezerras.models.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Product product;
    @JsonIgnore
    private Order order;
    private Byte quantity;
    private BigDecimal value;

    public OrderItem() {
    }

    public OrderItem(Long id, Product product, Order order, Byte quantity, BigDecimal value) {
        this.id = id;
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Byte getQuantity() {
        return quantity;
    }

    public void setQuantity(Byte quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(product, orderItem.product);
    }

    @Override
    public String toString() {
        return "OrderItem [id=" + id + ", product=" + (Objects.nonNull(product) ? product.getId() : "null")
                + ", order=" + (Objects.nonNull(order) ? order.getId() : "null") + ", quantity=" + quantity
                + ", value=" + value + "]";
    }


}

package com.pgbezerra.bezerras.models.dto;

import java.io.Serializable;

public class OrderItemDTO implements Serializable {

    private static final long serialVersionUID = 3487864289109034174L;

    private Integer product;
    private Byte quantity;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Integer product, Byte quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Byte getQuantity() {
        return quantity;
    }

    public void setQuantity(Byte quantity) {
        this.quantity = quantity;
    }
}

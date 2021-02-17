package com.pgbezerra.bezerras.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

public class MenuItemDTO implements Serializable {


    private static final long serialVersionUID = 2894635067092600276L;

    @Positive
    @NotNull(message = "Product not be null")
    private Integer product;
    @Positive
    @NotNull(message = "Menu not be null")
    private Long menu;

    public MenuItemDTO() {
    }

    public MenuItemDTO(Integer product, Long menu) {
        this.product = product;
        this.menu = menu;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Long getMenu() {
        return menu;
    }

    public void setMenu(Long menu) {
        this.menu = menu;
    }
}

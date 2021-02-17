package com.pgbezerra.bezerras.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;

public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "Product name not be empty or null")
    private String name;
    @NotNull(message = "Product value not be null")
    @Positive(message = "Product value not be less than 0.1")
    private BigDecimal value;
    @NotNull(message = "Product category not be null")
    @Positive(message = "Category value not be less or equals 0")
    private Integer category;

    public ProductDTO() {
    }

    public ProductDTO(String name, BigDecimal value, Integer category) {
        this.name = name;
        this.value = value;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }
}

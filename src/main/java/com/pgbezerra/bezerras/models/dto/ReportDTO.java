package com.pgbezerra.bezerras.models.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ReportDTO implements Serializable {

    private Date date;
    private BigDecimal amount;
    private Integer totalOrders;

    public ReportDTO() {
    }

    public ReportDTO(Date date, BigDecimal amount, Integer totalOrders) {
        this.date = date;
        this.amount = amount;
        this.totalOrders = totalOrders;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
}

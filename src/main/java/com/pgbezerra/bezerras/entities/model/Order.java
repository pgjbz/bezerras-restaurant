package com.pgbezerra.bezerras.entities.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;

public class Order implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date date;
	private BigDecimal value;
	private BigDecimal deliveryValue;
	private List<OrderItem> items = new ArrayList<>();;
	private OrderStatus orderStatus;
	private OrderType orderType;
	private OrderAddress orderAddress;
	
	public Order() {
	}

	public Order(Integer id, Date date, BigDecimal value, BigDecimal deliveryValue, List<OrderItem> items,
			OrderStatus orderStatus, OrderType orderType, OrderAddress orderAddress) {
		this.id = id;
		this.date = date;
		this.value = value;
		this.deliveryValue = deliveryValue;
		this.items = items;
		this.orderStatus = orderStatus;
		this.orderType = orderType;
		this.orderAddress = orderAddress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	
	public BigDecimal getDeliveryValue() {
		return deliveryValue;
	}

	public void setDeliveryValue(BigDecimal deliveryValue) {
		this.deliveryValue = deliveryValue;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}
	
	public OrderAddress getOrderAddress() {
		return orderAddress;
	}

	public void setOrderAddress(OrderAddress orderAddress) {
		this.orderAddress = orderAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", date=" + date + ", value=" + value + ", orderStatus=" + orderStatus + "]";
	}
	

}

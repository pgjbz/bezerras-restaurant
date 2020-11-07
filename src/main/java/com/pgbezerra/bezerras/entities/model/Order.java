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
	
	private Long id;
	private Date date;
	private BigDecimal value;
	private BigDecimal deliveryValue;
	private Table table;
	private List<OrderItem> items = new ArrayList<>();
	private OrderStatus orderStatus;
	private OrderType orderType;
	private OrderAddress orderAddress;
	
	public Order() {
	}

	public Order(Long id, Date date, BigDecimal value, BigDecimal deliveryValue, Table table, OrderStatus orderStatus,
			OrderType orderType, OrderAddress orderAddress) {
		this.id = id;
		this.date = date;
		this.value = value;
		this.deliveryValue = deliveryValue;
		this.table = table;
		this.orderStatus = orderStatus;
		this.orderType = orderType;
		this.orderAddress = orderAddress;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	
	public BigDecimal getDeliveryValue() {
		return deliveryValue;
	}

	public void setDeliveryValue(BigDecimal deliveryValue) {
		this.deliveryValue = deliveryValue;
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public List<OrderItem> getItems() {
		return items;
	}
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = OrderStatus.getByStatusCode(orderStatus);
	}
	
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = OrderType.getByOrderTypeCode(orderType);
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
		return "Order [id=" + id + ", date=" + date + ", value=" + value + ", deliveryValue=" + deliveryValue
				+ ", table=" + table + ", items=" + items + ", orderStatus=" + orderStatus + ", orderType=" + orderType
				+ ", orderAddress=" + orderAddress + "]";
	}

	

}

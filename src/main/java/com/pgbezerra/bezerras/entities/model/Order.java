package com.pgbezerra.bezerras.entities.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;

public class Order implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Date date;
	private BigDecimal value;
	private OrderStatus orderStatus;
	private OrderType orderType;
	
	public Order() {
	}

	public Order(Integer id, Date date, BigDecimal value, OrderStatus orderStatus) {
		this.id = id;
		this.date = date;
		this.value = value;
		this.orderStatus = orderStatus;
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

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
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

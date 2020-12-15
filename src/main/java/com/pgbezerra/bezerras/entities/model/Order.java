package com.pgbezerra.bezerras.entities.model;

import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order implements Serializable{

	private static final Logger LOG = Logger.getLogger(Order.class);

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

	public void calcOrderValue(){
		BigDecimal finalValue = BigDecimal.ZERO;
		for (OrderItem orderItem : items)
			finalValue = finalValue.add(orderItem.getValue().multiply(BigDecimal.valueOf(orderItem.getQuantity().longValue())));
		if (this.getOrderType() == OrderType.DELIVERY) {
			LOG.info(String.format("Delivery order %s", this.toString()));
			if(Objects.nonNull(this.getDeliveryValue()) && this.getDeliveryValue().intValue() > 0) {
				LOG.info(String.format("Delivery value %s", this.getDeliveryValue()));
				finalValue = finalValue.add(this.getDeliveryValue());
			} else {
				LOG.info("Default delivery value 5.0");
				BigDecimal defaultValue = BigDecimal.valueOf(5.0);
				this.setDeliveryValue(defaultValue);
				finalValue = finalValue.add(defaultValue);
			}
		}
		setValue(finalValue);
		LOG.info(String.format("Final value of order %s: %s", this.toString(), finalValue));
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

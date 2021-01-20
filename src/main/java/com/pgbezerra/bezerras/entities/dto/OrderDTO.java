package com.pgbezerra.bezerras.entities.dto;

import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.services.validation.OrderInsert;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@OrderInsert
public class OrderDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private BigDecimal deliveryValue;
	private Integer table;
	private final List<OrderItemDTO> items = new ArrayList<>();
	private OrderType orderType;
	private String clientName;
	private String street;
	private String number;
	private String complement;
	private String district;
	private String city;
	private String state;

	public BigDecimal getDeliveryValue() {
		return deliveryValue;
	}

	public void setDeliveryValue(BigDecimal deliveryValue) {
		this.deliveryValue = deliveryValue;
	}

	public Integer getTable() {
		return table;
	}

	public void setTable(Integer table) {
		this.table = table;
	}

	public List<OrderItemDTO> getItems() {
		return items;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}

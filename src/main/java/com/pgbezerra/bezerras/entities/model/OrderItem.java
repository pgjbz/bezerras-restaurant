package com.pgbezerra.bezerras.entities.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Product product;
	private Order order;
	private Byte quantity;
	private BigDecimal value;
	
	public OrderItem() {
	}

	public OrderItem(Integer id, Product product, Order order, Byte quantity, BigDecimal value) {
		this.id = id;
		this.product = product;
		this.order = order;
		this.quantity = quantity;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderItem other = (OrderItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", product=" + product + ", order=" + order + ", quantity=" + quantity
				+ ", value=" + value + "]";
	}
	

}

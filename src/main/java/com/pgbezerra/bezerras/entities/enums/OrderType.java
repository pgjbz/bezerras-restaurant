package com.pgbezerra.bezerras.entities.enums;

public enum OrderType {
	
	DELIVERY(1), TABLE(2), DESK(3);
	
	private Integer orderTypeCode;
	
	private OrderType(Integer orderTypeCode) {
		this.orderTypeCode = orderTypeCode;
	}
	
	public Integer getOrderTypeCode() {
		return this.orderTypeCode;
	}
	
	public static OrderType getByOrderTypeCode(Integer orderTypeCode) {
		if (orderTypeCode <= 0)
			throw new IllegalArgumentException(String.format("Invalid status code[%s], status must be greater than 0", orderTypeCode));
		for(OrderType type: OrderType.values())
			if(type.getOrderTypeCode().equals(orderTypeCode))
				return type;
		throw new IllegalArgumentException(String.format("Invalid status code[%s], status not found", orderTypeCode));
	}
}

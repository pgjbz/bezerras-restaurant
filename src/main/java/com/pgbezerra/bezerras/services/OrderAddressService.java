package com.pgbezerra.bezerras.services;

import java.util.List;

import com.pgbezerra.bezerras.entities.model.OrderAddress;

public interface OrderAddressService {
	
	OrderAddress insert(OrderAddress obj);
	Boolean update(OrderAddress obj);
	List<OrderAddress> findAll();
	OrderAddress findById(Long id);
	Boolean deleteById(Long id);
	
}

package com.pgbezerra.bezerras.services.impl;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.services.OrderAddressService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;

@Service
public class OrderAddressServiceImpl implements OrderAddressService {
	
	private static final Logger LOG = Logger.getLogger(OrderAddressServiceImpl.class);

	@Autowired
	private OrderAddressRepository orderAddressesRepository;
	
	public OrderAddressServiceImpl(OrderAddressRepository orderAddressesRepository) {
		this.orderAddressesRepository = orderAddressesRepository;
	}

	@Override
	public OrderAddress insert(OrderAddress obj) {
		obj.setId(null);
		return orderAddressesRepository.insert(obj);
	}

	@Override
	public Boolean update(OrderAddress obj) {
		OrderAddress oldObj = findById(obj.getId());
		updateDate(oldObj, obj);
		Boolean updated = orderAddressesRepository.update(oldObj);
		LOG.info(String.format("OrderAddress %s updated: %s", obj, updated));
		return updated;
	}

	private void updateDate(OrderAddress oldObj, OrderAddress obj) {
		oldObj.setCity(obj.getCity());
		oldObj.setStreet(obj.getStreet());
		oldObj.setClientName(obj.getClientName());
		oldObj.setDistrict(obj.getDistrict());
		oldObj.setState(obj.getState());
		oldObj.setNumber(obj.getNumber());
		oldObj.setComplement(obj.getComplement());
	}

	@Override
	public List<OrderAddress> findAll() {
		List<OrderAddress> orderAddressess = orderAddressesRepository.findAll();
		LOG.info(String.format("%s orderAddressess found", orderAddressess.size()));
		if(!orderAddressess.isEmpty()) 
			return orderAddressess;
		throw new ResourceNotFoundException("No orderAddressess found");
	}

	@Override
	public OrderAddress findById(Long id) {
		Optional<OrderAddress> orderAddresses = orderAddressesRepository.findById(id);
		LOG.info(String.format("OrderAddress with id %s found: ", orderAddresses.isPresent()));
		if(orderAddresses.isPresent())
			return orderAddresses.get();
		throw new ResourceNotFoundException(String.format("No orderAddressess found with id: %s", id));
	}

	@Override
	public Boolean deleteById(Long id) {
		findById(id);
		Boolean deleted = orderAddressesRepository.deleteById(id);
		LOG.info(String.format("OrderAddress %s deleted: %s", id, deleted));
		return deleted;
	}

}

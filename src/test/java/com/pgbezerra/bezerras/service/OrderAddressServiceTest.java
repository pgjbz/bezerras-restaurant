package com.pgbezerra.bezerras.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.repository.OrderAddressRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.OrderAddressService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.OrderAddressServiceImpl;

@RunWith(SpringRunner.class)
public class OrderAddressServiceTest {
	
	@TestConfiguration
	static class OrderAddressServiceTestConfigurarion {
		@Bean
		public OrderAddressService orderAddressService(OrderAddressRepository orderAddressRepository) {
			return new OrderAddressServiceImpl(orderAddressRepository);
		}
	}
	
	private OrderAddress oa1;
	private OrderAddress oa2;
	
	@Before
	public void start() {
		oa1 = new OrderAddress(1L, "Client A", "Street A", "1", "District A", "City A", "State A");
		oa2 = new OrderAddress(2L, "Client B", "Street B", "1", "District B", "City B", "State B");
	}
	
	@Autowired
	private OrderAddressService orderAddressService;
	
	@MockBean
	private OrderAddressRepository orderAddressRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentOrderAddressExpectedException() {
		Mockito.when(orderAddressRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
		orderAddressService.findById(1L);
	}
	
	@Test
	public void findOrderAddressExpectedSuccess() {
		Mockito.when(orderAddressRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(oa1));
		OrderAddress table = orderAddressService.findById(1L);
		Assert.notNull(table, "OrderAddress not be null");
		Mockito.verify(orderAddressRepository).findById(Mockito.anyLong());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateInexistentOrderAddressExpectedError() {
		orderAddressService.update(oa1);
	}
	
	@Test
	public void updateOrderAddressExpectedSuccess() {
		
		Mockito.when(orderAddressRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(oa1));
		Mockito.when(orderAddressRepository.update(oa1)).thenReturn(Boolean.TRUE);
		
		Boolean success = orderAddressService.update(oa1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(orderAddressRepository).findById(Mockito.anyLong());
		Mockito.verify(orderAddressRepository).update(oa1);
	}
	
	@Test
	public void insertOrderAddressExpectedSuccess() {
		
		oa1.setId(null);
		
		Mockito.when(orderAddressRepository.insert(Mockito.any())).thenReturn(oa2);
		
		oa1 = orderAddressService.insert(oa1);
		
		Assert.isTrue(!oa1.getId().equals(0), "Id not be 0");
		Mockito.verify(orderAddressRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertOrderAddressExpectedException() {
		OrderAddress obj = new OrderAddress(1L, null, null, "1", "District A", "City A", "State A");
		
		Mockito.when(orderAddressRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		orderAddressService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(orderAddressRepository.findAll()).thenReturn(new ArrayList<>());
		orderAddressService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<OrderAddress> tables = new ArrayList<>();
		tables.add(oa1);
		tables.add(oa2);
		Mockito.when(orderAddressRepository.findAll()).thenReturn(tables);
		tables = orderAddressService.findAll();
		Assert.notEmpty(tables, "Return not be empty");
		Mockito.verify(orderAddressRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		orderAddressService.deleteById(1L);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(orderAddressRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(oa1));
		Mockito.when(orderAddressRepository.deleteById(1L)).thenReturn(Boolean.TRUE);
		Boolean deleted = orderAddressService.deleteById(1L);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(orderAddressRepository).findById(Mockito.anyLong());
		Mockito.verify(orderAddressRepository).deleteById(Mockito.anyLong());
	}
	

}

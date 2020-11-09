package com.pgbezerra.bezerras.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.pgbezerra.bezerras.entities.model.OrderAddress;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrderAddressRespositoryTest {
	
	@Autowired
	private OrderAddressRepository orderAddressRepository;
	
	private List<OrderAddress> orderAddresses = new ArrayList<>();
	
	{
		OrderAddress oa1 = new OrderAddress(null, "Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		OrderAddress oa2 = new OrderAddress(null, "Client B", "Rua b", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddresses.addAll(Arrays.asList(oa1, oa2));
	}
	
	@Test
	public void insertNewOrderAddressExpectedSuccessAndReturnNewPK() {
		OrderAddress orderAddress = new OrderAddress(null, "Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddressRepository.insert(orderAddress);
		
		Assert.assertNotEquals(null, orderAddress.getId());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertNewOrderAddressWithOutStreetExpectedException() {
		OrderAddress orderAddress = new OrderAddress(null, "Client A", null, "123", "Centro", "Sao Paulo", "Sao Paulo");
		orderAddressRepository.insert(orderAddress);
	}
	
	@Test
	public void findAllCategoriesExpectedSuccess() {
		orderAddressRepository.insertAll(orderAddresses);
		List<OrderAddress> orderAddresses = orderAddressRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", orderAddresses.size() > 0);
	}
	
	@Test
	public void findAllCategoriesExpectedNoReturn() {
		List<OrderAddress> orderAddresses = orderAddressRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", orderAddresses.size() <= 0);
	}
	
	@Test
	public void findByIdExpectedNoReturn() {
		Assert.assertFalse(orderAddressRepository.findById(999L).isPresent());
	}
	
	@Test
	public void findByIdExpectedReturn() {
		orderAddressRepository.insertAll(orderAddresses);
		Assert.assertTrue(orderAddressRepository.findById(1L).isPresent());
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderAddressWithoutStreetExpectedError() {
		
		orderAddressRepository.insertAll(orderAddresses);
		OrderAddress orderAddress = orderAddressRepository.findById(1L).get();
		orderAddress.setStreet(null);
		orderAddressRepository.update(orderAddress);
	}
	
	@Test
	public void updateOrderAddressExpectedSuccess() {
		
		orderAddressRepository.insertAll(orderAddresses);
		OrderAddress orderAddress = orderAddressRepository.findById(1L).get();
		orderAddress.setStreet("Fashion");
		Assert.assertTrue(orderAddressRepository.update(orderAddress));
	}
	
	@Test
	public void updateOrderAddressExpectedNoUpdate() {
		OrderAddress orderAddress = new OrderAddress(999L, "Client A", "Rua A", "123", "Centro", "Sao Paulo", "Sao Paulo");
		Assert.assertFalse(orderAddressRepository.update(orderAddress));
	}
	
	@Test
	public void deleteByIdExpectedNoDelete() {
		Assert.assertFalse(orderAddressRepository.deleteById(999L));
	}
	
	@Test
	public void deleteByIdExpectedDeleteSuccess() {
		orderAddressRepository.insertAll(orderAddresses);
		Assert.assertTrue(orderAddressRepository.deleteById(1L));
	}
	

}

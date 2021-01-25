package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.entities.enums.OrderStatus;
import com.pgbezerra.bezerras.entities.enums.OrderType;
import com.pgbezerra.bezerras.entities.model.*;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class OrderItemRepositoryTest {

	private List<Category> categories = new ArrayList<>();
	private List<Product> products = new ArrayList<>();
	private List<Order> orders = new ArrayList<>();
	private List<OrderItem> orderItems = new ArrayList<>();
	private List<Table> tables = new ArrayList<>();
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private TableRepository tableRepository;
	
	
	
	{
		Category c1 = new Category(null, "Food");
		Category c2 = new Category(null, "Drink");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Product p2 = new Product(null, "Beer", BigDecimal.valueOf(25.0), c2);
		Product p3 = new Product(null, "Baiao de 2", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d) ,BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		Order o3 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, null, OrderStatus.DOING, OrderType.DESK, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		OrderItem oi2 = new OrderItem(null, p2, o1, Byte.valueOf("2"), BigDecimal.valueOf(30.0));
		Table t1 = new Table(1, "Table 1");
		Table t2 = new Table(2, "Table 2");
		orders.addAll(Arrays.asList(o1, o3));
		categories.addAll(Arrays.asList(c1, c2));
		products.addAll(Arrays.asList(p1, p2, p3));
		orderItems.addAll(Arrays.asList(oi1, oi2));
		tables.addAll(Arrays.asList(t1, t2));
	}
	
	@Test
	public void inserOrderitemExpectedSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		Assert.assertTrue(oi1.getId() > 0L);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserOrderitemExpectedError() {
		Category c1 = new Category(null, "Food");
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, null, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
	}
	
	@Test
	public void findAllOrderItemExpectedSuccess() {
		tableRepository.insertAll(tables);
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		orderRepository.insertAll(orders);
		orderItemRepository.insertAll(orderItems);
		List<OrderItem> orderItems = orderItemRepository.findAll();
		Assert.assertTrue(orderItems.size() > 0);
	}
	
	@Test
	public void findAllOrderExpectedError() {
		List<OrderItem> orderItems = orderItemRepository.findAll();
		Assert.assertFalse(orderItems.size() > 0);
	}
	
	@Test
	public void findOrderItemByIdExpectSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		Assert.assertTrue(orderItemRepository.findById(oi1.getId()).isPresent());
	}
	
	@Test
	public void findOrderItemByIdExpectedError() {
		Assert.assertTrue(orderItemRepository.findById(1L).isEmpty());
	}
	
	@Test
	public void updateOrderItemExpectSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"),  OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		OrderItem orderItem = orderItemRepository.findById(oi1.getId()).get();
		oi1.setValue(BigDecimal.valueOf(30d));
		Assert.assertTrue(orderItemRepository.update(oi1));;
		oi1 = orderItemRepository.findById(oi1.getId()).get();
		Assert.assertNotEquals(orderItem.getValue(), oi1.getValue());
	}
	
	@Test(expected = DatabaseException.class)
	public void updateOrderItemExpectError() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		oi1.setProduct(null);
		orderItemRepository.update(oi1);
	}
	
	@Test
	public void deleteItemByIdExpectedSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Order o1 = new Order(null, new Date(), BigDecimal.valueOf(20d), BigDecimal.ZERO, new Table(1, "Table 1"), OrderStatus.DOING, OrderType.TABLE, null);
		OrderItem oi1 = new OrderItem(null, p1, o1, Byte.valueOf("1"), BigDecimal.valueOf(25.0));
		Table t1 = new Table(1, "Table 1");
		tableRepository.insert(t1);
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		orderRepository.insert(o1);
		orderItemRepository.insert(oi1);
		Assert.assertTrue(orderItemRepository.deleteById(oi1.getId()));
	}
	
	@Test
	public void deleteItemByIdExpectedError() {
		Assert.assertFalse(orderItemRepository.deleteById(999L));
	}
	
	@Test
	public void findOrderItemByOrderExpectedSuccess() {
		tableRepository.insertAll(tables);
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		orderRepository.insertAll(orders);
		orderItemRepository.insertAll(orderItems);
		List<OrderItem> orderItems = orderItemRepository.findByOrder(orders.get(0));
		Assert.assertTrue(orderItems.size() > 0);
	}
	
	@Test
	public void findOrderItemByOrderExpectedNoReturn() {
		List<OrderItem> orderItems = orderItemRepository.findByOrder(orders.get(0));
		Assert.assertTrue(orderItems.size() <= 0);
	}
	

}

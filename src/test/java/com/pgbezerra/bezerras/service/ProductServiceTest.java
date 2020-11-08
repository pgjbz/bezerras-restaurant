package com.pgbezerra.bezerras.service;

import java.math.BigDecimal;
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

import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
import com.pgbezerra.bezerras.repository.ProductRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.ProductService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.ProductServiceImpl;

@RunWith(SpringRunner.class)
public class ProductServiceTest {
	
	@TestConfiguration
	static class ProductServiceTestConfigurarion {
		@Bean
		public ProductService categoryService(ProductRepository productRepository, CategoryService categoryService) {
			return new ProductServiceImpl(productRepository, categoryService);
		}
	}
	
	private Category c1;
	private Category c2;
	private Product p1;
	private Product p2;
	
	@Before
	public void start() {
		c1 = new Category(1,"Food");
		c2 = new Category(2, "Drink");
		p1 = new Product(1, "Feijoada", BigDecimal.valueOf(25.0), c1);
		p2 = new Product(2, "Beer", BigDecimal.valueOf(4.0), c2);
	}
	
	@Autowired
	private ProductService productService;
	
	@MockBean
	private CategoryService categoryService;
	
	@MockBean
	private ProductRepository productRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentProductExpectedException() {
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		productService.findById(1);
	}
	
	@Test
	public void findProductExpectedSuccess() {
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(p1));
		Product product = productService.findById(1);
		Assert.notNull(product, "Product not be null");
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateInexistentProductExpectedError() {
		productService.update(p1);
	}
	
	@Test
	public void updateProductExpectedSuccess() {
		
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(p1));
		Mockito.when(productRepository.update(p1)).thenReturn(Boolean.TRUE);
		
		Boolean success = productService.update(p1);
		
		Assert.isTrue(success, "Expected true");
	}
	
	@Test
	public void insertProductExpectedSuccess() {
		
		Mockito.when(productRepository.insert(p1)).thenReturn(p2);
		
		p1 = productService.insert(p1);
		
		Assert.isTrue(!p1.getId().equals(0), "Id not be 0");
	}
	
	@Test(expected = RuntimeException.class)
	public void insertProductExpectedException() {
		Product obj = new Product(null, null, null, null);
		
		Mockito.when(productRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		productService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(productRepository.findAll()).thenReturn(new ArrayList<>());
		productService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<Product> products = new ArrayList<>();
		products.add(p1);
		products.add(p2);
		Mockito.when(productRepository.findAll()).thenReturn(products);
		products = productService.findAll();
		Assert.notEmpty(products, "Return not be empty");
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		productService.deleteById(1);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(productRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(p1));
		Mockito.when(productRepository.deleteById(1)).thenReturn(Boolean.TRUE);
		Boolean deleted = productService.deleteById(1);
		Assert.isTrue(deleted, "Expected no delete");
	}
	

}

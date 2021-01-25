package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Product;
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
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class ProductRepositoryTest {

	private List<Category> categories = new ArrayList<>();
	private List<Product> products = new ArrayList<>();
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	
	
	{
		Category c1 = new Category(null, "Food");
		Category c2 = new Category(null, "Drink");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Product p2 = new Product(null, "Beer", BigDecimal.valueOf(25.0), c2);
		Product p3 = new Product(null, "Baiao de 2", BigDecimal.valueOf(25.0), c1);
		categories.addAll(Arrays.asList(c1, c2));
		products.addAll(Arrays.asList(p1, p2, p3));
	}
	
	@Test(expected = DatabaseException.class)
	public void insertProductWithouCategoryExpectedError() {
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), null);
		productRepository.insert(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserProductWithoutCategoryIdExpectedError() {
		Category category = new Category(null, "Food");
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserProductWithInexistentCategoryExpetecSuccess() {
		Category category = new Category(1, "Food");
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
	}
	
	@Test
	public void insertProductExpectedSuccess() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		Assert.assertTrue(product.getId() > 0);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateProductWithouNameExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		product.setName(null);
		productRepository.update(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateProductWithoutValueExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		product.setValue(null);
		productRepository.update(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateProductWithoutCategoryExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		product.setCategory(null);
		productRepository.update(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateProductWithoutAllValuesExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		product.setName(null);
		product.setValue(null);
		product.setCategory(null);
		productRepository.update(product);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateProductWithInexistentCategoryExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		category.setId(999);
		productRepository.update(product);
	}
	
	@Test
	public void updateProductExpectedSuccess() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		product.setName("Baiao de Dois");
		Assert.assertTrue(productRepository.update(product));
	}
	
	@Test
	public void updateInexistentProductExpectedNoUpdates() {
		Product product = new Product(999, "Feijoada", BigDecimal.valueOf(25.0), null);
		product.setName("Baiao de Dois");
		Assert.assertFalse(productRepository.update(product));
	}
	
	@Test
	public void deleteProductExpectedNoSuccess() {
		Assert.assertFalse(productRepository.deleteById(999));
	}
	
	@Test
	public void deleteProductExpectedSuccess() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		Assert.assertTrue(productRepository.deleteById(product.getId()));
	}
	
	@Test
	public void findAllExpectedNoReturn() {
		List<Product> products = productRepository.findAll();
		Assert.assertEquals(0, products.size());
	}
	
	@Test
	public void findAllExpectReturn() {
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		List<Product> products = productRepository.findAll();
		for(Product p: products)
			System.out.println(p.toString());
		Assert.assertNotEquals(0, products.size());
	}
	
	@Test
	public void findByIdInexsistentProductExpectedError() {
		Optional<Product> product = productRepository.findById(999);
		Assert.assertFalse(product.isPresent());
	}
	
	@Test
	public void findByIdExpectedSuccess() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		Optional<Product> productReturn = productRepository.findById(product.getId());
		Assert.assertTrue(productReturn.isPresent());
	}
	
	@Test
	public void findProductByCategoryExpectedSuccess() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		Product product = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), category);
		productRepository.insert(product);
		List<Product> products = productRepository.findByCategory(category);
		Assert.assertNotEquals(0, products.size());
	}
	
	@Test
	public void findProductByCategoryExpectedError() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		List<Product> products = productRepository.findByCategory(category);
		Assert.assertEquals(0, products.size());
	}
	

}

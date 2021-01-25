package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.entities.model.Category;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class CategoryRespositoryTest {

	@Autowired
	private CategoryRepository categoryRepository;
	
	private List<Category> categories = new ArrayList<>();
	
	{
		Category c1 = new Category(null, "Food");
		Category c2 = new Category(null, "Drink");
		categories.addAll(Arrays.asList(c1, c2));
	}
	
	@Test
	public void insertNewCategoryExpectedSuccessAndReturnNewPK() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		
		Assert.assertNotEquals(null, category.getId());
	}
	
	@Test
	public void insertNewCategoryWithoutisMenuValueExpectedSuccessAndReturnNewPK() {
		Category category = new Category(null, "Food");
		categoryRepository.insert(category);
		category = categoryRepository.findById(category.getId()).get();
		
		Assert.assertNotEquals(null, category.getId());
	}
	
	@Test
	public void insertNewCategoryWithisMenuValueTrueExpectedSuccessAndReturnNewPK() {
		Category category = new Category(null, "Food");
		category.setIsMenu(true);
		categoryRepository.insert(category);
		category = categoryRepository.findById(category.getId()).get();
		
		Assert.assertNotEquals(null, category.getId());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertNewCategoryWithOutNameExpectedException() {
		Category category = new Category(null, null);
		categoryRepository.insert(category);
	}
	
	@Test
	public void findAllCategoriesExpectedSuccess() {
		categoryRepository.insertAll(categories);
		List<Category> categories = categoryRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", categories.size() > 0);
	}
	
	@Test
	public void findAllCategoriesExpectedNoReturn() {
		List<Category> categories = categoryRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", categories.size() <= 0);
	}
	
	@Test
	public void findByIdExpectedNoReturn() {
		Assert.assertFalse(categoryRepository.findById(999).isPresent());
	}
	
	@Test
	public void findByIdExpectedReturn() {
		categoryRepository.insertAll(categories);
		Assert.assertTrue(categoryRepository.findById(1).isPresent());
	}
	
	@Test(expected = DatabaseException.class)
	public void updateCategoryWithoutNameExpectedError() {
		
		categoryRepository.insertAll(categories);
		Category category = categoryRepository.findById(1).get();
		category.setName(null);
		categoryRepository.update(category);
	}
	
	@Test
	public void updateCategoryExpectedSuccess() {
		
		categoryRepository.insertAll(categories);
		Category category = categoryRepository.findById(1).get();
		category.setName("Fashion");
		Assert.assertTrue(categoryRepository.update(category));
	}
	
	@Test
	public void updateCategoryExpectedNoUpdate() {
		Category category = new Category(999, "Fashion");
		Assert.assertFalse(categoryRepository.update(category));
	}
	
	@Test
	public void deleteByIdExpectedNoDelete() {
		Assert.assertFalse(categoryRepository.deleteById(999));
	}
	
	@Test
	public void deleteByIdExpectedDeleteSuccess() {
		categoryRepository.insertAll(categories);
		Assert.assertTrue(categoryRepository.deleteById(1));
	}
	

}

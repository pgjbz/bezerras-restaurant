package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.entity.Category;
import com.pgbezerra.bezerras.repository.CategoryRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.CategoryService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.CategoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@Import(BCryptConfiguration.class)
public class CategoryServiceTest {
	
	@TestConfiguration
	static class CategoryServiceTestConfigurarion {
		@Bean
		public CategoryService categoryService(CategoryRepository categoryRepository) {
			return new CategoryServiceImpl(categoryRepository);
		}
	}
	
	private Category c1;
	private Category c2;
	
	@Before
	public void start() {
		c1 = new Category(1,"Food");
		c2 = new Category(1, "Food");
	}
	
	@Autowired
	private CategoryService categoryService;
	
	@MockBean
	private CategoryRepository categoryRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentCategoryExpectedException() {
		Mockito.when(categoryRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		categoryService.findById(1);
	}
	
	@Test
	public void findCategoryExpectedSuccess() {
		Mockito.when(categoryRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(c1));
		Category category = categoryService.findById(1);
		Assert.notNull(category, "Product not be null");
		Mockito.verify(categoryRepository).findById(Mockito.anyInt());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateInexistentCategoryExpectedError() {
		categoryService.update(c1);
	}
	
	@Test
	public void updateCategoryExpectedSuccess() {
		
		Mockito.when(categoryRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(c1));
		Mockito.when(categoryRepository.update(c1)).thenReturn(Boolean.TRUE);
		
		Boolean success = categoryService.update(c1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(categoryRepository).findById(Mockito.anyInt());
		Mockito.verify(categoryRepository).update(c1);
	}
	
	@Test
	public void insertCategoryExpectedSuccess() {
		
		Mockito.when(categoryRepository.insert(c1)).thenReturn(c2);
		
		c1 = categoryService.insert(c1);
		
		Assert.isTrue(!c1.getId().equals(0), "Id not be 0");
		
		Mockito.verify(categoryRepository).insert(Mockito.any());
	}
	
	@Test
	public void insertCategoryWithMenuValueTrueExpectedSuccess() {
		
		Mockito.when(categoryRepository.insert(c1)).thenReturn(c2);
		
		c1.setIsMenu(true);
		c1 = categoryService.insert(c1);
		Assert.isTrue(!c1.getId().equals(0), "Id not be 0");
		
		Mockito.verify(categoryRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertCategoryExpectedException() {
		Category obj = new Category(null, null);
		
		Mockito.when(categoryRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		categoryService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(categoryRepository.findAll()).thenReturn(new ArrayList<>());
		categoryService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<Category> categories = new ArrayList<>();
		categories.add(c1);
		categories.add(c2);
		Mockito.when(categoryRepository.findAll()).thenReturn(categories);
		categories = categoryService.findAll();
		Assert.notEmpty(categories, "Return not be empty");
		Mockito.verify(categoryRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		categoryService.deleteById(1);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(categoryRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(c1));
		Mockito.when(categoryRepository.deleteById(1)).thenReturn(Boolean.TRUE);
		Boolean deleted = categoryService.deleteById(1);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(categoryRepository).findById(Mockito.anyInt());
		Mockito.verify(categoryRepository).deleteById(1);
	}
	

}

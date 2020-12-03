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

import com.pgbezerra.bezerras.entities.enums.DayOfWeek;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.repository.MenuRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.MenuServiceImpl;

@RunWith(SpringRunner.class)
public class MenuServiceTest {
	
	@TestConfiguration
	static class MenuServiceTestConfigurarion {
		@Bean
		public MenuService menuService(MenuRepository menuRepository) {
			return new MenuServiceImpl(menuRepository);
		}
	}
	
	private Menu m1;
	private Menu m2;
	
	@Before
	public void start() {
		m1 = new Menu(1L,"Food", DayOfWeek.FRIDAY);
		m2 = new Menu(2L, "Food", DayOfWeek.SATURDAY);
	}
	
	@Autowired
	private MenuService menuService;
	
	@MockBean
	private MenuRepository menuRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentMenuExpectedException() {
		Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null));
		menuService.findById(1L);
	}
	
	@Test
	public void findMenuExpectedSuccess() {
		Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(m1));
		Menu menu = menuService.findById(1L);
		Assert.notNull(menu, "Product not be null");
		Mockito.verify(menuRepository).findById(Mockito.anyLong());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateInexistentMenuExpectedError() {
		menuService.update(m1);
	}
	
	@Test
	public void updateMenuExpectedSuccess() {
		
		Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(m1));
		Mockito.when(menuRepository.update(m1)).thenReturn(Boolean.TRUE);
		
		Boolean success = menuService.update(m1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(menuRepository).findById(Mockito.anyLong());
		Mockito.verify(menuRepository).update(m1);
	}
	
	@Test
	public void insertMenuExpectedSuccess() {
		
		Mockito.when(menuRepository.insert(m1)).thenReturn(m2);
		
		m1 = menuService.insert(m1);
		
		Assert.isTrue(!m1.getId().equals(0), "Id not be 0");
		
		Mockito.verify(menuRepository).insert(Mockito.any());
	}
	
	@Test
	public void insertMenuWithMenuValueTrueExpectedSuccess() {
		
		Mockito.when(menuRepository.insert(m1)).thenReturn(m2);
		
		m1 = menuService.insert(m1);
		Assert.isTrue(!m1.getId().equals(0), "Id not be 0");
		
		Mockito.verify(menuRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertMenuExpectedException() {
		Menu obj = new Menu(null, null, null);
		
		Mockito.when(menuRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		menuService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(menuRepository.findAll()).thenReturn(new ArrayList<>());
		menuService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<Menu> categories = new ArrayList<>();
		categories.add(m1);
		categories.add(m2);
		Mockito.when(menuRepository.findAll()).thenReturn(categories);
		categories = menuService.findAll();
		Assert.notEmpty(categories, "Return not be empty");
		Mockito.verify(menuRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		menuService.deleteById(1L);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(menuRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(m1));
		Mockito.when(menuRepository.deleteById(1L)).thenReturn(Boolean.TRUE);
		Boolean deleted = menuService.deleteById(1L);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(menuRepository).findById(Mockito.anyLong());
		Mockito.verify(menuRepository).deleteById(1L);
	}
	

}

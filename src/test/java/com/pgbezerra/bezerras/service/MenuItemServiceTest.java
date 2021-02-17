package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.entity.Category;
import com.pgbezerra.bezerras.models.entity.Menu;
import com.pgbezerra.bezerras.models.entity.MenuItem;
import com.pgbezerra.bezerras.models.entity.Product;
import com.pgbezerra.bezerras.repository.MenuItemRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.MenuItemService;
import com.pgbezerra.bezerras.services.MenuService;
import com.pgbezerra.bezerras.services.ProductService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.MenuItemServiceImpl;
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

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@Import(BCryptConfiguration.class)
public class MenuItemServiceTest {
	
	@TestConfiguration
	static class MenuItemServiceTestConfigurarion {
		@Bean
		public MenuItemService menuItemItemService(MenuItemRepository menuItemRepository, MenuService menuService, ProductService productService) {
			return new MenuItemServiceImpl(menuItemRepository, menuService, productService);
		}
	}
	
	private MenuItem mi1;
	private MenuItem mi2;
	private Product p1;
	private Product p2;
	private Menu m1;
	private Menu m2;
	
	@Before
	public void start() {
		Category c1 = new Category(1, "Category 1");
		c1.setIsMenu(true);
		p1 = new Product(1, "Feijoada", BigDecimal.valueOf(25.0), c1);
		p2 = new Product(2, "Baiao de Dois", BigDecimal.valueOf(25.0), c1);
		m1 = new Menu(1L,"Food", DayOfWeek.FRIDAY);
		m2 = new Menu(2L,"Food", DayOfWeek.SATURDAY);
		mi1 = new MenuItem(m1, p1);
		mi2 = new MenuItem(m2, p2);

	}
	
	@Autowired
	private MenuItemService menuItemService;
	
	@MockBean
	private MenuItemRepository menuItemRepository;
	
	@MockBean
	private MenuService menuService;
	
	@MockBean
	private ProductService productService;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentMenuItemExpectedException() {
		Mockito.when(menuItemRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(null));
		menuItemService.findById(m1, p1);
	}
	
	@Test
	public void findMenuItemExpectedSuccess() {
		Mockito.when(menuItemRepository.findById(Mockito.anyMap())).thenReturn(Optional.ofNullable(mi1));
		MenuItem menuItem = menuItemService.findById(m1, p1);
		Assert.notNull(menuItem, "Product not be null");
		Mockito.verify(menuItemRepository).findById(Mockito.anyMap());
	}
	
	@Test
	public void insertMenuItemExpectedSuccess() {
		
		Mockito.when(menuItemRepository.insert(mi1)).thenReturn(mi1);
		Mockito.when(menuService.findById(Mockito.anyLong())).thenReturn(m1);
		Mockito.when(productService.findById(Mockito.anyInt())).thenReturn(p1);
		mi1 = menuItemService.insert(mi1);
		
		Mockito.verify(menuItemRepository).insert(mi1);
	}
	
	
	@Test(expected = DatabaseException.class)
	public void insertMenuItemExpectedException() {
		MenuItem obj = new MenuItem(m1, p1);
		Mockito.when(productService.findById(p1.getId())).thenReturn(p1);
		Mockito.when(menuItemRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		menuItemService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(menuItemRepository.findAll()).thenReturn(new ArrayList<>());
		menuItemService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<MenuItem> menuItems = new ArrayList<>();
		menuItems.add(mi1);
		menuItems.add(mi2);
		Mockito.when(menuItemRepository.findAll()).thenReturn(menuItems);
		menuItems = menuItemService.findAll();
		Assert.notEmpty(menuItems, "Return not be empty");
		Mockito.verify(menuItemRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		menuItemService.deleteById(m1, p1);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(menuItemRepository.findById(Mockito.anyMap())).thenReturn(Optional.ofNullable(mi1));
		Mockito.when(menuItemRepository.deleteById(Mockito.anyMap())).thenReturn(Boolean.TRUE);
		Boolean deleted = menuItemService.deleteById(m1, p1);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(menuItemRepository).findById(Mockito.anyMap());
		Mockito.verify(menuItemRepository).deleteById(Mockito.anyMap());
	}
	

}

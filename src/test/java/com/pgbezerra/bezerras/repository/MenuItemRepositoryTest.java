package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.entities.model.Category;
import com.pgbezerra.bezerras.entities.model.Menu;
import com.pgbezerra.bezerras.entities.model.MenuItem;
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
import java.time.DayOfWeek;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class MenuItemRepositoryTest {
	
	private List<Category> categories = new ArrayList<>();
	private List<Product> products = new ArrayList<>();
	private List<Menu> menus = new ArrayList<>();
	private List<MenuItem> menuItems = new ArrayList<>();
	
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	{
		Category c1 = new Category(null, "Food");
		Category c2 = new Category(null, "Drink");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Product p2 = new Product(null, "Beer", BigDecimal.valueOf(25.0), c2);
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		Menu m2 = new Menu(null, "Segundou", DayOfWeek.MONDAY);
		Menu m3 = new Menu(null, "Segundou", DayOfWeek.TUESDAY);
		Menu m4 = new Menu(null, "Segundou", DayOfWeek.WEDNESDAY);
		Menu m5 = new Menu(null, "Segundou", DayOfWeek.THURSDAY);
		Menu m6 = new Menu(null, "Segundou", DayOfWeek.FRIDAY);
		Menu m7 = new Menu(null, "Segundou", DayOfWeek.SATURDAY);
		MenuItem mi1 = new MenuItem(m1, p1);
		MenuItem mi2 = new MenuItem(m2, p2);
		categories.addAll(Arrays.asList(c1, c2));
		products.addAll(Arrays.asList(p1, p2));
		menus.addAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7));
		menuItems.addAll(Arrays.asList(mi1, mi2));
	}
	
	@Test
	public void insertMenuItemExpectedSuccess() {
		Category c1 = new Category(null, "Food");
		Product p1 = new Product(null, "Feijoada", BigDecimal.valueOf(25.0), c1);
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		MenuItem mi1 = new MenuItem(m1, p1);
		categoryRepository.insert(c1);
		productRepository.insert(p1);
		menuRepository.insert(m1);
		menuItemRepository.insert(mi1);
		Assert.assertNotNull(mi1.getMenu());
		Assert.assertNotNull(mi1.getProduct());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertMenuItemExpectedError() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		Product p1 = new Product(1, "Feijoada", BigDecimal.valueOf(25.0), null);
		MenuItem mi1 = new MenuItem(m1, p1);
		menuRepository.insert(m1);
		menuItemRepository.insert(mi1);
	}
	
	@Test
	public void findAllExpectedSuccess() {
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		menuRepository.insertAll(menus);
		menuItemRepository.insertAll(menuItems);
		List<MenuItem> menuItems = menuItemRepository.findAll();
		Assert.assertFalse(menuItems.isEmpty());
	}
	
	@Test
	public void findAllExpectedNoReturn() {
		List<MenuItem> menuItems = menuItemRepository.findAll();
		Assert.assertTrue(menuItems.isEmpty());
	}
	
	@Test
	public void findByIdExpectedSuccess() {
		categoryRepository.insertAll(categories);
		productRepository.insertAll(products);
		menuRepository.insertAll(menus);
		menuItemRepository.insertAll(menuItems);
		Map<Menu, Product> id = new HashMap<>();
		id.put(menus.get(0), products.get(0));
		Assert.assertTrue(menuItemRepository.findById(id).isPresent());
	}
	
	@Test
	public void findByIdExpectedNoReturn() {
		Map<Menu, Product> id = new HashMap<>();
		id.put(menus.get(0), products.get(0));
		Assert.assertTrue(menuItemRepository.findById(id).isEmpty());
	}
	

}

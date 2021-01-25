package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.entities.model.Menu;
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

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class MenuRepositoryTest {

	private List<Menu> menus = new ArrayList<>();
	
	@Autowired
	private MenuRepository menuRepository;
	
	{
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		Menu m2 = new Menu(null, "Segundou", DayOfWeek.MONDAY);
		Menu m3 = new Menu(null, "Segundou", DayOfWeek.TUESDAY);
		Menu m4 = new Menu(null, "Segundou", DayOfWeek.WEDNESDAY);
		Menu m5 = new Menu(null, "Segundou", DayOfWeek.THURSDAY);
		Menu m6 = new Menu(null, "Segundou", DayOfWeek.FRIDAY);
		Menu m7 = new Menu(null, "Segundou", DayOfWeek.SATURDAY);
		menus.addAll(Arrays.asList(m1, m2, m3, m4, m5, m6, m7));
	}
	
	@Test
	public void insertMenuWithouNameAddressExpectedSuccess() {
		Menu m1 = new Menu(null, null, DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		Assert.assertTrue(m1.getId() > 0L);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserMenuWithoutDayOfWeekExpectedError() {
		Menu m1 = new Menu(null, "Segundou", null);
		menuRepository.insert(m1);
	}
	
	@Test
	public void insertMenuExpectedSuccess() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		m1 = menuRepository.findById(m1.getId()).get();
		Assert.assertTrue(m1.getId() > 0L);
	}
	
	@Test(expected = DateTimeException.class)
	public void updateMenuWithInvalidDayOfWeekValueExpectedError() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		m1 = menuRepository.findById(m1.getId()).get();
		m1.setDayOfWeek(999);
		menuRepository.update(m1);
	}
	
	@Test
	public void updateMenuExpectedSuccess() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		m1 = menuRepository.findById(m1.getId()).get();
		m1.setName("SEXTOU");
		m1.setDayOfWeek(DayOfWeek.FRIDAY.getValue());
		Assert.assertTrue(menuRepository.update(m1));
	}
	
	@Test
	public void updateInexistentMenuExpectedNoUpdates() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		m1.setName("SEXTOU");
		m1.setDayOfWeek(DayOfWeek.FRIDAY.getValue());
		Assert.assertFalse(menuRepository.update(m1));
	}
	
	@Test
	public void deleteMenuExpectedNoSuccess() {
		Assert.assertFalse(menuRepository.deleteById(999L));
	}
	
	@Test
	public void deleteMenuExpectedSuccess() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		Assert.assertTrue(menuRepository.deleteById(m1.getId()));
	}
	
	@Test
	public void findAllExpectedNoReturn() {
		List<Menu> menus = menuRepository.findAll();
		Assert.assertTrue(menus.isEmpty());
	}
	
	@Test
	public void findAllExpectReturn() {
		menuRepository.insertAll(menus);
		List<Menu> menus = menuRepository.findAll();
		Assert.assertFalse(menus.isEmpty());
	}
	
	@Test
	public void findByIdInexsistentMenuExpectedError() {
		Assert.assertFalse(menuRepository.findById(1L).isPresent());
	}
	
	@Test
	public void findByIdExpectedSuccess() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		m1 = menuRepository.findById(m1.getId()).get();
		Assert.assertTrue(m1.getId() > 0L);
	}
	
	@Test
	public void findByDayOfWeekExpectedSuccess() {
		Menu m1 = new Menu(null, "Segundou", DayOfWeek.SUNDAY);
		menuRepository.insert(m1);
		Assert.assertTrue(menuRepository.findByDayOfWeek(DayOfWeek.SUNDAY).isPresent());
	}
	
	@Test
	public void findByDayOfWeekExpectedError() {
		Assert.assertFalse(menuRepository.findByDayOfWeek(DayOfWeek.SUNDAY).isPresent());
	}
	

}

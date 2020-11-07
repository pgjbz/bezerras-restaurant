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

import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class TableRespositoryTest {

	@Autowired
	private TableRepository tableRepository;

	private static final List<Table> tables = new ArrayList<>();

	{
		Table c1 = new Table(null, "Food");
		Table c2 = new Table(null, "Drink");
		tables.addAll(Arrays.asList(c1, c2));
	}

	@Test
	public void insertNewTableExpectedSuccessAndReturnNewPK() {
		Table table = new Table(null, "Food");
		tableRepository.insert(table);

		Assert.assertNotEquals(null, table.getId());
	}

	@Test(expected = DatabaseException.class)
	public void insertNewTableWithOutNameExpectedException() {
		Table table = new Table(null, null);
		tableRepository.insert(table);
	}

	@Test
	public void findAllTablesExpectedSuccess() {
		tableRepository.insertAll(TableRespositoryTest.tables);
		List<Table> tables = tableRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", tables.size() > 0);
	}

	@Test
	public void findAllTablesExpectedNoReturn() {
		List<Table> tables = tableRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", tables.size() <= 0);
	}

	@Test
	public void findByIdExpectedNoReturn() {
		Assert.assertFalse(tableRepository.findById(999).isPresent());
	}

	@Test
	public void findByIdExpectedReturn() {
		tableRepository.insertAll(TableRespositoryTest.tables);
		Assert.assertTrue(tableRepository.findById(1).isPresent());
	}

	@Test(expected = DatabaseException.class)
	public void updateTableWithoutNameExpectedError() {

		tableRepository.insertAll(TableRespositoryTest.tables);
		Table table = tableRepository.findById(1).get();
		table.setName(null);
		tableRepository.update(table);
	}

	@Test
	public void updateTableExpectedSuccess() {

		tableRepository.insertAll(TableRespositoryTest.tables);
		Table table = tableRepository.findById(1).get();
		table.setName("Fashion");
		Assert.assertTrue(tableRepository.update(table));
	}

	@Test
	public void updateTableExpectedNoUpdate() {
		Table table = new Table(999, "Fashion");
		Assert.assertFalse(tableRepository.update(table));
	}

	@Test
	public void deleteByIdExpectedNoDelete() {
		Assert.assertFalse(tableRepository.deleteById(999));
	}

	@Test
	public void deleteByIdExpectedDeleteSuccess() {
		tableRepository.insertAll(TableRespositoryTest.tables);
		Assert.assertTrue(tableRepository.deleteById(1));
	}

}

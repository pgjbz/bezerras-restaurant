package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.model.Table;
import com.pgbezerra.bezerras.repository.TableRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.TableService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.TableServiceImpl;
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
public class TableServiceTest {
	
	@TestConfiguration
	static class TableServiceTestConfigurarion {
		@Bean
		public TableService tableService(TableRepository tableRepository) {
			return new TableServiceImpl(tableRepository);
		}
	}
	
	private Table t1;
	private Table t2;
	
	@Before
	public void start() {
		t1 = new Table(1,"Table 1");
		t2 = new Table(1, "Table 2");
	}
	
	@Autowired
	private TableService tableService;
	
	@MockBean
	private TableRepository tableRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findInexsistentTableExpectedException() {
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		tableService.findById(1);
	}
	
	@Test
	public void findTableExpectedSuccess() {
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(t1));
		Table table = tableService.findById(1);
		Assert.notNull(table, "Table not be null");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateInexistentTableExpectedError() {
		tableService.update(t1);
	}
	
	@Test
	public void updateTableExpectedSuccess() {
		
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(t1));
		Mockito.when(tableRepository.update(t1)).thenReturn(Boolean.TRUE);
		
		Boolean success = tableService.update(t1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
		Mockito.verify(tableRepository).update(t1);
	}
	
	@Test
	public void insertTableExpectedSuccess() {
		
		t1.setId(null);
		
		Mockito.when(tableRepository.insert(Mockito.any())).thenReturn(t2);
		
		t1 = tableService.insert(t1);
		
		Assert.isTrue(!t1.getId().equals(0), "Id not be 0");
		Mockito.verify(tableRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertTableExpectedException() {
		Table obj = new Table(null, null);
		
		Mockito.when(tableRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		tableService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(tableRepository.findAll()).thenReturn(new ArrayList<>());
		tableService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<Table> tables = new ArrayList<>();
		tables.add(t1);
		tables.add(t2);
		Mockito.when(tableRepository.findAll()).thenReturn(tables);
		tables = tableService.findAll();
		Assert.notEmpty(tables, "Return not be empty");
		Mockito.verify(tableRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		tableService.deleteById(1);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(t1));
		Mockito.when(tableRepository.deleteById(1)).thenReturn(Boolean.TRUE);
		Boolean deleted = tableService.deleteById(1);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
		Mockito.verify(tableRepository).deleteById(Mockito.anyInt());
	}
	

}

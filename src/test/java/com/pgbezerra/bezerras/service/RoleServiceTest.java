package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.repository.RoleRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.RoleService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.RoleServiceImpl;
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
public class RoleServiceTest {
	
	@TestConfiguration
	static class RoleServiceTestConfiguration {
		@Bean
		public RoleService tableService(RoleRepository tableRepository) {
			return new RoleServiceImpl(tableRepository);
		}
	}
	
	private Role r1;
	private Role r2;
	
	@Before
	public void start() {
		r1 = new Role(1,"ROLE_ADMIN");
		r2 = new Role(1, "ROLE_EMPL");
	}
	
	@Autowired
	private RoleService tableService;
	
	@MockBean
	private RoleRepository tableRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findNonexistentRoleExpectedException() {
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		tableService.findById(1);
	}
	
	@Test
	public void findRoleExpectedSuccess() {
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Role table = tableService.findById(1);
		Assert.notNull(table, "Role not be null");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateNonexistentRoleExpectedError() {
		tableService.update(r1);
	}
	
	@Test
	public void updateRoleExpectedSuccess() {
		
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Mockito.when(tableRepository.update(r1)).thenReturn(Boolean.TRUE);
		
		Boolean success = tableService.update(r1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
		Mockito.verify(tableRepository).update(r1);
	}
	
	@Test
	public void insertRoleExpectedSuccess() {
		
		r1.setId(null);
		
		Mockito.when(tableRepository.insert(Mockito.any())).thenReturn(r2);
		
		r1 = tableService.insert(r1);
		
		Assert.isTrue(!r1.getId().equals(0), "Id not be 0");
		Mockito.verify(tableRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertRoleExpectedException() {
		Role obj = new Role(null, null);
		
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
		List<Role> tables = new ArrayList<>();
		tables.add(r1);
		tables.add(r2);
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
		Mockito.when(tableRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Mockito.when(tableRepository.deleteById(1)).thenReturn(Boolean.TRUE);
		Boolean deleted = tableService.deleteById(1);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(tableRepository).findById(Mockito.anyInt());
		Mockito.verify(tableRepository).deleteById(Mockito.anyInt());
	}
	

}

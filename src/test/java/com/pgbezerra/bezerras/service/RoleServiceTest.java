package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.models.entity.Role;
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
		public RoleService roleService(RoleRepository roleRepository) {
			return new RoleServiceImpl(roleRepository);
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
	private RoleService roleService;
	
	@MockBean
	private RoleRepository roleRepository;
	
	@Test(expected = ResourceNotFoundException.class)
	public void findNonexistentRoleExpectedException() {
		Mockito.when(roleRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(null));
		roleService.findById(1);
	}
	
	@Test
	public void findRoleExpectedSuccess() {
		Mockito.when(roleRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Role table = roleService.findById(1);
		Assert.notNull(table, "Role not be null");
		Mockito.verify(roleRepository).findById(Mockito.anyInt());
	}
	
	@Test(expected =  ResourceNotFoundException.class)
	public void updateNonexistentRoleExpectedError() {
		roleService.update(r1);
	}
	
	@Test
	public void updateRoleExpectedSuccess() {
		
		Mockito.when(roleRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Mockito.when(roleRepository.update(r1)).thenReturn(Boolean.TRUE);
		
		Boolean success = roleService.update(r1);
		
		Assert.isTrue(success, "Expected true");
		Mockito.verify(roleRepository).findById(Mockito.anyInt());
		Mockito.verify(roleRepository).update(r1);
	}
	
	@Test
	public void insertRoleExpectedSuccess() {
		
		r1.setId(null);
		
		Mockito.when(roleRepository.insert(Mockito.any())).thenReturn(r2);
		
		r1 = roleService.insert(r1);
		
		Assert.isTrue(!r1.getId().equals(0), "Id not be 0");
		Mockito.verify(roleRepository).insert(Mockito.any());
	}
	
	@Test(expected = DatabaseException.class)
	public void insertRoleExpectedException() {
		Role obj = new Role(null, null);
		
		Mockito.when(roleRepository.insert(obj)).thenThrow(DatabaseException.class);
		
		roleService.insert(obj);
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedResourceNotFoundException() {
		Mockito.when(roleRepository.findAll()).thenReturn(new ArrayList<>());
		roleService.findAll();
	}
	
	@Test
	public void findAllExpectedSuccess() {
		List<Role> tables = new ArrayList<>();
		tables.add(r1);
		tables.add(r2);
		Mockito.when(roleRepository.findAll()).thenReturn(tables);
		tables = roleService.findAll();
		Assert.notEmpty(tables, "Return not be empty");
		Mockito.verify(roleRepository).findAll();
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedResourceNotFoundException() {
		roleService.deleteById(1);
	}
	
	@Test
	public void deleteByIdExpectedReturnTrue() {
		Mockito.when(roleRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(r1));
		Mockito.when(roleRepository.deleteById(1)).thenReturn(Boolean.TRUE);
		Boolean deleted = roleService.deleteById(1);
		Assert.isTrue(deleted, "Expected no delete");
		Mockito.verify(roleRepository).findById(Mockito.anyInt());
		Mockito.verify(roleRepository).deleteById(Mockito.anyInt());
	}
	

}

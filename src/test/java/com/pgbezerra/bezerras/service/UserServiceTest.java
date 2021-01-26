package com.pgbezerra.bezerras.service;

import com.pgbezerra.bezerras.configuration.BCryptConfiguration;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.entities.model.User;
import com.pgbezerra.bezerras.repository.UserRepository;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import com.pgbezerra.bezerras.services.RoleService;
import com.pgbezerra.bezerras.services.UserService;
import com.pgbezerra.bezerras.services.exception.ResourceNotFoundException;
import com.pgbezerra.bezerras.services.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({BCryptConfiguration.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class UserServiceTest {

	@TestConfiguration
	static class UserServiceTestConfiguration {
		@Bean
		public UserService userServiceTest(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
			return new UserServiceImpl(userRepository, roleService, bCryptPasswordEncoder);
		}
	}

	private final List<Role> roles = new ArrayList<>();
	private final List<User> users = new ArrayList<>();
	
	@Autowired
	private UserService userServiceTest;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private RoleService roleService;
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	
	{
		//Long id, String name, String username, String password, Role role
		Role r1 = new Role(1, "ROLE_ADMIN");
		Role r2 = new Role(2, "ROLE_EMPL");
		User u1 = new User(1L, "Admin", "Admin", "admin", r1);
		User u2 = new User(1L, "Employee 1", "Employee1", "employee1", r2);
		User u3 = new User(1L, "Employee 2", "Employee2", "employee2", r2);
		roles.addAll(Arrays.asList(r1, r2));
		users.addAll(Arrays.asList(u1, u2, u3));
	}

	@Test
	public void insertNewUserExpectedSuccess(){
		User u1 = users.get(0);
		Mockito.when(userRepository.insert(u1)).thenReturn(users.get(0));
		Mockito.when(roleService.findById(1)).thenReturn(roles.get(0));
		userServiceTest.insert(u1);
		Mockito.verify(userRepository).insert(u1);
		Mockito.verify(roleService).findById(1);
	}

	@Test(expected = DatabaseException.class)
	public void insertNewUserExpectedError(){
		Mockito.when(userRepository.insert(Mockito.any(User.class))).thenThrow(DatabaseException.class);
		userServiceTest.insert(users.get(0));
	}

	@Test
	public void findUserByIdExpectedSuccess(){
		Long id = 1L;
		Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(users.get(0)));
		Assertions.assertNotNull(userServiceTest.findById(id));
		Mockito.verify(userRepository).findById(id);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void findUserByIdExpectedError(){
		userServiceTest.findById(1L);
	}

	@Test
	public void findUserByUsernameExpectedSuccess(){
		String username = "Admin";
		Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.ofNullable(users.get(0)));
		Assertions.assertNotNull(userServiceTest.loadUserByUsername(username));
		Mockito.verify(userRepository).findByUsername(username);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void findUserByUsernameExpectedError(){
		String username = "Admin";
		userServiceTest.loadUserByUsername(username);
	}

	@Test
	public void findAllExpectedSuccess(){
		Mockito.when(userRepository.findAll()).thenReturn(users);
		List<User> users = userServiceTest.findAll();
		Assertions.assertFalse(users.isEmpty());
		Mockito.verify(userRepository).findAll();
	}

	@Test(expected = ResourceNotFoundException.class)
	public void findAllExpectedError(){
		List<User> users = userServiceTest.findAll();
	}

	@Test
	public void deleteByIdExpectedSuccess(){
		Long id = 1L;
		Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(users.get(0)));
		Mockito.when(userRepository.deleteById(id)).thenReturn(Boolean.TRUE);
		Assertions.assertTrue(userServiceTest.deleteById(id));
		Mockito.verify(userRepository).deleteById(id);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deleteByIdExpectedError(){
		userServiceTest.deleteById(1L);
	}

	@Test
	public void updateUserExpectedSuccess(){
		Long id = 1L;
		User u1 = users.get(0);
		Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(u1));
		Mockito.when(userRepository.update(u1)).thenReturn(Boolean.TRUE);
		Assertions.assertTrue(userServiceTest.update(u1));
		Mockito.verify(userRepository).update(u1);
		Mockito.verify(userRepository).findById(id);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void updateUserExpectedResourceNotFound(){
		User u1 = users.get(0);
		userServiceTest.update(u1);
	}

	@Test(expected = DatabaseException.class)
	public void updateUserExpectedError(){
		Long id = 1L;
		User u1 = users.get(0);
		Mockito.when(userRepository.findById(id)).thenReturn(Optional.ofNullable(u1));
		Mockito.when(userRepository.update(u1)).thenThrow(DatabaseException.class);
		u1.setRole(null);
		userServiceTest.update(u1);
	}


}

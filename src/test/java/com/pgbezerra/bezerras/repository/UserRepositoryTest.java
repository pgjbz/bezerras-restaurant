package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.entities.model.Role;
import com.pgbezerra.bezerras.entities.model.User;
import com.pgbezerra.bezerras.repository.exception.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
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
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class UserRepositoryTest {

	private List<Role> roles = new ArrayList<>();
	private List<User> users = new ArrayList<>();
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	
	{
		//Long id, String name, String username, String password, Role role
		Role r1 = new Role(1, "ROLE_ADMIN");
		Role r2 = new Role(2, "ROLE_EMPL");
		User u1 = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), r1);
		User u2 = new User(1L, "Employee 1", "Employee1", bCryptPasswordEncoder.encode("employee1"), r2);
		User u3 = new User(1L, "Employee 2", "Employee2", bCryptPasswordEncoder.encode("employee2"), r2);
		roles.addAll(Arrays.asList(r1, r2));
		users.addAll(Arrays.asList(u1, u2, u3));
	}
	
	@Test(expected = DatabaseException.class)
	public void insertUserWithouRoleExpectedError() {
		User user = new User(1L, "Employee 1", "Employee1", bCryptPasswordEncoder.encode("employee1"), null);
		userRepository.insert(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void inserUserWithoutRoleIdExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void insertUserWithoutExistentRoleExpectedError() {
		Role role = new Role(3, "ROLE_ADMIN");
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
	}

	@Test
	public void insertUserExpectedSuccess() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		Assert.assertTrue(user.getId() > 0);
	}

	@Test(expected = DatabaseException.class)
	public void insertUserWithoutNameExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, null, "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateUserWithoutNameExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		user.setName(null);
		userRepository.update(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateUserWithoutPasswordExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		user.setPassword(null);
		userRepository.update(user);
	}

	@Test(expected = DatabaseException.class)
	public void updateUserWithoutRoleExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		user.setRole(null);
		userRepository.update(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateUserWithoutAllValuesExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		user.setName(null);
		user.setPassword(null);
		user.setUsername(null);
		user.setRole(null);
		userRepository.update(user);
	}
	
	@Test(expected = DatabaseException.class)
	public void updateUserWithoutExistentRoleExpectedError() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		role.setId(999);
		userRepository.update(user);
	}
	
	@Test
	public void updateUserExpectedSuccess() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		user.setName("Fullstack Employee");
		Assert.assertTrue(userRepository.update(user));
	}
	
	@Test
	public void updateNonexistentUserExpectedNoUpdates() {
		Role role = new Role(1, "ROLE_ADMIN");
		User user = new User(3L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		user.setName("NO EMPL");
		Assert.assertFalse(userRepository.update(user));
	}
	
	@Test
	public void deleteUserExpectedNoSuccess() {
		Assert.assertFalse(userRepository.deleteById(999L));
	}
	
	@Test
	public void deleteUserExpectedSuccess() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		Assert.assertTrue(userRepository.deleteById(user.getId()));
	}
	
	@Test
	public void findAllExpectReturn() {
		roleRepository.insertAll(roles);
		userRepository.insertAll(users);
		List<User> users = userRepository.findAll();
		for(User p: users)
			System.out.println(p.toString());
		Assert.assertNotEquals(0, users.size());
	}
	
	@Test
	public void findByIdNonexistentUserExpectedError() {
		Optional<User> user = userRepository.findById(999L);
		Assert.assertFalse(user.isPresent());
	}
	
	@Test
	public void findByIdExpectedSuccess() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		Optional<User> userReturn = userRepository.findById(user.getId());
		Assert.assertTrue(userReturn.isPresent());
	}

	@Test
	public void findByUsernameNonexistentUserExpectedError() {
		Optional<User> user = userRepository.findByUsername("Admin");
		Assert.assertFalse(user.isPresent());
	}

	@Test
	public void findByUsernameExpectedSuccess() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);
		User user = new User(1L, "Admin", "Admin", bCryptPasswordEncoder.encode("admin"), role);
		userRepository.insert(user);
		Optional<User> userReturn = userRepository.findByUsername(user.getUsername());
		Assert.assertTrue(userReturn.isPresent());
	}


}

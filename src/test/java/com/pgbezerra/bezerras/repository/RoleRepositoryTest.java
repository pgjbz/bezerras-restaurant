package com.pgbezerra.bezerras.repository;

import com.pgbezerra.bezerras.config.EncoderConfig;
import com.pgbezerra.bezerras.models.entity.Role;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import({EncoderConfig.class})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class RoleRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	private List<Role> roles = new ArrayList<>();

	{
		Role r1 = new Role(null, "ROLE_ADMIN");
		Role r2 = new Role(null, "ROLE_EMPL");
		roles.addAll(Arrays.asList(r1, r2));
	}

	@Test
	public void insertNewRoleExpectedSuccessAndReturnNewPK() {
		Role role = new Role(null, "ROLE_ADMIN");
		roleRepository.insert(role);

		Assert.assertNotEquals(null, role.getId());
	}

	@Test(expected = DatabaseException.class)
	public void insertNewRoleWithOutNameExpectedException() {
		Role role = new Role(null, null);
		roleRepository.insert(role);
	}

	@Test
	public void findAllRolesExpectedSuccess() {
		roleRepository.insertAll(roles);
		List<Role> roles = roleRepository.findAll();
		Assert.assertTrue("Expected more than 1 item", roles.size() > 0);
	}

	@Test
	public void findByIdExpectedNoReturn() {
		Assert.assertFalse(roleRepository.findById(999).isPresent());
	}

	@Test
	public void findByIdExpectedReturn() {
		roleRepository.insertAll(roles);
		Assert.assertTrue(roleRepository.findById(1).isPresent());
	}

	@Test(expected = DatabaseException.class)
	public void updateRoleWithoutNameExpectedError() {

		roleRepository.insertAll(roles);
		Role role = roleRepository.findById(1).get();
		role.setRoleName(null);
		roleRepository.update(role);
	}

	@Test
	public void updateRoleExpectedSuccess() {

		roleRepository.insertAll(roles);
		Role role = roleRepository.findById(1).get();
		role.setRoleName("ROLE_ADMIN");
		Assert.assertTrue(roleRepository.update(role));
	}

	@Test
	public void updateRoleExpectedNoUpdate() {
		Role role = new Role(999, "ROLE_ADMIN");
		Assert.assertFalse(roleRepository.update(role));
	}

	@Test
	public void deleteByIdExpectedNoDelete() {
		Assert.assertFalse(roleRepository.deleteById(999));
	}

	@Test
	public void deleteByIdExpectedDeleteSuccess() {
		roleRepository.insertAll(roles);
		Assert.assertTrue(roleRepository.deleteById(1));
	}

}

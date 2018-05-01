package nl.michaelv.registerandlogin.repository;

import nl.michaelv.model.Role;
import nl.michaelv.repository.RoleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

	@Autowired
	private RoleRepository roleRepository;

	private Role role;

	@Before
	public void before() {
		role = new Role();
		role.setName("ROLE");
		roleRepository.save(role);
	}

	@After
	public void after() {
		roleRepository.deleteAll();
	}

	@Test
	public void findRoleByName() {
		Role role = roleRepository.findByName("ROLE");
		assertNotNull(role);
		assertTrue(role.getName().equals("ROLE"));
	}

	@Test
	public void findRoleByNameWithUnknownValue() {
		Role role = roleRepository.findByName("OTHER");
		assertNull(role);
	}

}

package nl.michaelv.registerandlogin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import nl.michaelv.registerandlogin.model.Role;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private RoleRepository repo;

	@Test
	public void findRoleByName() {
		// Given
		Role role = getRole();
		entityManager.merge(role);
		entityManager.flush();

		// When
		Role found = repo.findByName(role.getName());

		// Then
		assertThat(found.getName()).isEqualTo(role.getName());
		assertThat(found.getId()).isEqualTo(role.getId());
	}

	@Test
	public void findRoleByName_NotFound() {
		// When
		Role found = repo.findByName("OTHER");

		// Then
		assertThat(found).isEqualTo(null);
	}

	private Role getRole() {
		Role r = new Role();
		r.setId(1);
		r.setName("USER");

		return r;
	}
}

package nl.michaelv.registerandlogin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import nl.michaelv.registerandlogin.model.Role;
import nl.michaelv.registerandlogin.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository repo;

	@Test
	public void findUserByEmail() {
		// Given
		User user = getUser();
		entityManager.merge(user);
		entityManager.flush();

		// When
		User found = repo.findByEmail(user.getEmail());

		// Then
		assertThat(found.getName()).isEqualTo(user.getName());
		assertThat(found.getEmail()).isEqualTo(user.getEmail());
	}

	@Test
	public void findUserByEmail_NotFound() {
		// When
		User found = repo.findByEmail("notexisting@someprovider.com");

		// Then
		assertThat(found).isEqualTo(null);
	}

	private User getUser() {
		User u = new User();
		u.setId(1);
		u.setName("User1");
		u.setEmail("user1@someprovider.com");
		u.setPassword("123456");
		u.setActive(true);
		u.addRole(getRole());

		return u;
	}

	private Role getRole() {
		Role r = new Role();
		r.setId(1);
		r.setName("USER");

		return r;
	}
}

package nl.michaelv.registerandlogin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.repository.UserRepository;

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
		assertThat(found.getFirstName()).isEqualTo(user.getFirstName());
		assertThat(found.getMiddleName()).isEqualTo(user.getMiddleName());
		assertThat(found.getLastName()).isEqualTo(user.getLastName());
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
		u.setFirstName("User1");
		u.setMiddleName("von");
		u.setLastName("Lastname");
		u.setEmail("user1@someprovider.com");
		u.setPassword("123456");
		u.setPasswordConfirmation("123456");
		u.setEnabled(true);
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

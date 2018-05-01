package nl.michaelv.registerandlogin.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import nl.michaelv.model.PasswordToken;
import org.junit.After;
import org.junit.Before;
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

	private static final String EMAIL = "some.user@provider.com";
	private static final String OTHER = "other.email@provider.com";

	@Autowired
	private UserRepository userRepository;

	private User user;

	@Before
	public void before() {
		user = new User();
		user.setFirstName("Some");
		user.setLastName("User");
		user.setEmail(EMAIL);
		user.setVerified(true);
		user.setPassword("123");
		user.setPhone("06123456789");
		user = userRepository.save(user);
	}

	@After
	public void after() {
		userRepository.deleteAll();
	}


	@Test
	public void findUserByEmail() {
		User user = userRepository.findByEmail(EMAIL);
		assertNotNull(user);
		assertTrue(user.getFirstName().equals("Some"));
		assertTrue(user.getLastName().equals("User"));
	}

	@Test
	public void findUserByEmailWithUnknownValue() {
		User user = userRepository.findByEmail(OTHER);
		assertNull(user);
	}

	@Test
	public void userExistsByEmail() {
		assertTrue(userRepository.existsByEmail(EMAIL));
	}

	@Test
	public void userExistsByEmailWithUnknownValue() {
		assertFalse(userRepository.existsByEmail(OTHER));
	}
}

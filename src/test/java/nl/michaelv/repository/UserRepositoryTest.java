package nl.michaelv.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import nl.michaelv.model.User;
import nl.michaelv.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

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
		user.setFirstName("User");
		user.setMiddleName("von");
		user.setLastName("Lastname");
		user.setEmail(EMAIL);
		user.setVerified(true);
		user.setPassword("123456");
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
		assertThat(user.getFirstName()).isEqualTo("User");
		assertThat(user.getMiddleName()).isEqualTo("von");
		assertThat(user.getLastName()).isEqualTo("Lastname");
		assertThat(user.getEmail()).isEqualTo(EMAIL);
		assertThat(user.getPassword()).isEqualTo("123456");
		assertThat(user.getPhone()).isEqualTo("06123456789");
		assertThat(user.isVerified()).isEqualTo(true);
	}

	@Test
	public void findUserByEmailWithUnknownValue() {
		assertNull(userRepository.findByEmail(OTHER));
	}

	@Test
	public void userExistsByEmail() {
		assertThat(userRepository.existsByEmail(EMAIL)).isEqualTo(true);
	}

	@Test
	public void userExistsByEmailWithUnknownValue() {
		assertThat(userRepository.existsByEmail(OTHER)).isEqualTo(false);
	}
}

package nl.michaelv.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.PasswordToken;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.repository.PasswordTokenRepository;
import nl.michaelv.repository.UserRepository;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PasswordTokenRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordTokenRepository passwordRepository;

	private User user;

	private PasswordToken token;

	@Before
	public void before() {
		user = new User();
		user.setFirstName("Some");
		user.setLastName("User");
		user.setEmail("some.user@provider.com");
		user.setVerified(true);
		user.setPassword("123");
		user.setPhone("06123456789");
		user = userRepository.save(user);

		passwordRepository.save(new PasswordToken("myCustomTokenId", user));
	}

	@After
	public void after() {
		passwordRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void findPasswordTokenByToken() {
		PasswordToken token = (PasswordToken) passwordRepository.findByToken("myCustomTokenId");
		assertNotNull(token);
		assertThat(token.getToken()).isEqualTo("myCustomTokenId");
		assertThat(token.getUser().getEmail()).isEqualTo("some.user@provider.com");
	}

	@Test
	public void findPasswordTokenByTokenWithUnknownValue() {
		PasswordToken token = (PasswordToken) passwordRepository.findByToken("someNonExistingToken");
		assertNull(token);
	}

	@Test
	public void findPasswordTokensByUser() {
		List<Token> tokens = passwordRepository.findByUser(user);
		assertNotNull(tokens);
		assertThat(tokens.size()).isEqualTo(1);
		assertThat(tokens.get(0).token()).isEqualTo("myCustomTokenId");

		passwordRepository.save(new PasswordToken("myOtherCustomTokenId", user));

		tokens = passwordRepository.findByUser(user);
		assertNotNull(tokens);
		assertThat(tokens.size()).isEqualTo(2);
		assertThat(tokens.get(0).token()).isEqualTo("myCustomTokenId");
		assertThat(tokens.get(1).token()).isEqualTo("myOtherCustomTokenId");
	}

	@Test
	public void findPasswordTokensByUserWithUnknownValue() {
		User user2 = new User();
		user2.setFirstName("First");
		user2.setLastName("Last");
		user2.setEmail("other@provider.com");
		user2.setVerified(true);
		user2.setPassword("123");
		user2.setPhone("06123456789");
		user2 = userRepository.save(user2);

		List<Token> tokens = passwordRepository.findByUser(user2);
		assertThat(tokens.size()).isEqualTo(0);
	}
}

package nl.michaelv.registerandlogin.repository;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;
import nl.michaelv.repository.PasswordTokenRepository;
import nl.michaelv.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

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

		token = new PasswordToken(user);
		token.setToken("myCustomTokenId");
		passwordRepository.save(token);
	}

	@After
	public void after() {
		passwordRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void findPasswordTokenByToken() {
		PasswordToken token = passwordRepository.findByToken("myCustomTokenId");
		assertNotNull(token);
		assertTrue(token.getToken().equals("myCustomTokenId"));
		assertTrue(token.getUser().getEmail().equals("some.user@provider.com"));
	}

	@Test
	public void findPasswordTokenByTokenWithUnknownValue() {
		PasswordToken token = passwordRepository.findByToken("someNonExistingToken");
		assertNull(token);
	}

	@Test
	public void findPasswordTokensByUser() {
		List<PasswordToken> tokens = passwordRepository.findByUser(user);
		assertNotNull(tokens);
		assertTrue(tokens.size() == 1);
		assertTrue(tokens.get(0).getToken() == "myCustomTokenId");

		PasswordToken token2 = new PasswordToken(user);
		token2.setToken("myOtherCustomTokenId");
		passwordRepository.save(token2);

		tokens = passwordRepository.findByUser(user);
		assertNotNull(tokens);
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getToken().equals("myCustomTokenId"));
		assertTrue(tokens.get(1).getToken().equals("myOtherCustomTokenId"));
	}

	@Test
	public void findPasswordTokensByUserWithUnknownValue() {
		User user2 = new User();
		user2.setFirstName("First");
		user2.setLastName("Last");
		user2.setVerified(true);
		user2.setPassword("123");
		user2.setPhone("06123456789");
		user2 = userRepository.save(user2);

		List<PasswordToken> tokens = passwordRepository.findByUser(user2);
		assertTrue(tokens.size() == 0);
	}
}

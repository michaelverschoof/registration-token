package nl.michaelv.registerandlogin.repository;

import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.repository.VerificationTokenRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VerificationTokenRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	private User user;
	private VerificationToken token;

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

		token = new VerificationToken(user);
		token.setToken("myCustomTokenId");
		verificationTokenRepository.save(token);
	}

	@After
	public void after() {
		verificationTokenRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void findVerificationTokenByToken() {
		VerificationToken token = verificationTokenRepository.findByToken("myCustomTokenId");
		assertNotNull(token);
		assertTrue(token.getToken().equals("myCustomTokenId"));
		assertTrue(token.getUser().getEmail().equals("some.user@provider.com"));
	}

	@Test
	public void findVerificationTokenByTokenWithUnknownValue() {
		VerificationToken token = verificationTokenRepository.findByToken("someNonExistingToken");
		assertNull(token);
	}

	@Test
	public void findVerificationTokensByUser() {
		List<VerificationToken> tokens = verificationTokenRepository.findByUser(user);
		assertNotNull(tokens);
		assertTrue(tokens.size() == 1);
		assertTrue(tokens.get(0).getToken() == "myCustomTokenId");

		VerificationToken token2 = new VerificationToken(user);
		token2.setToken("myOtherCustomTokenId");
		verificationTokenRepository.save(token2);

		tokens = verificationTokenRepository.findByUser(user);
		assertNotNull(tokens);
		assertTrue(tokens.size() == 2);
		assertTrue(tokens.get(0).getToken().equals("myCustomTokenId"));
		assertTrue(tokens.get(1).getToken().equals("myOtherCustomTokenId"));
	}

	@Test
	public void findVerificationTokensByUserWithUnknownValue() {
		User user2 = new User();
		user2.setId(2);
		user2.setFirstName("First");
		user2.setLastName("Last");
		user2.setVerified(true);
		user2.setPassword("123");
		user2.setPhone("06123456789");

		List<VerificationToken> tokens = verificationTokenRepository.findByUser(user2);
		assertTrue(tokens.size() == 0);
	}
}

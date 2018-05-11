package nl.michaelv.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.model.tokens.VerificationToken;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.repository.VerificationTokenRepository;

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

		token = new VerificationToken("myCustomTokenId", user);
		verificationTokenRepository.save(token);
	}

	@After
	public void after() {
		verificationTokenRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	public void findVerificationTokenByToken() {
		VerificationToken token = (VerificationToken) verificationTokenRepository.findByToken("myCustomTokenId");
		assertNotNull(token);
		assertThat(token.getToken()).isEqualTo("myCustomTokenId");
		assertThat(token.getUser().getEmail()).isEqualTo("some.user@provider.com");
	}

	@Test
	public void findVerificationTokenByTokenWithUnknownValue() {
		VerificationToken token = (VerificationToken) verificationTokenRepository.findByToken("someNonExistingToken");
		assertNull(token);
	}

	@Test
	public void findVerificationTokensByUser() {
		List<Token> tokens = verificationTokenRepository.findByUser(user);
		assertNotNull(tokens);
		assertThat(tokens.size()).isEqualTo(1);
		assertThat(tokens.get(0).token()).isEqualTo("myCustomTokenId");

		VerificationToken token2 = new VerificationToken("myOtherCustomTokenId", user);
		verificationTokenRepository.save(token2);

		tokens = verificationTokenRepository.findByUser(user);
		assertNotNull(tokens);
		assertThat(tokens.size()).isEqualTo(2);
		assertThat(tokens.get(0).token()).isEqualTo("myCustomTokenId");
		assertThat(tokens.get(1).token()).isEqualTo("myOtherCustomTokenId");
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

		List<Token> tokens = verificationTokenRepository.findByUser(user2);
		assertThat(tokens.size()).isEqualTo(0);
	}
}

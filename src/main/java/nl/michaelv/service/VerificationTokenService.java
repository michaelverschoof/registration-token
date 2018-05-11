package nl.michaelv.service;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.model.tokens.VerificationToken;
import nl.michaelv.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("verificationTokenService")
public class VerificationTokenService implements TokenService {

	@Autowired
	private VerificationTokenRepository repository;

	public Token findByToken(String token) {
		return this.repository.findByToken(token);
	}

	public List<Token> findByUser(User user) {
		return this.repository.findByUser(user);
	}

	public VerificationToken create(User user) {
		VerificationToken verificationToken = new VerificationToken(user);
		return repository.save(verificationToken);
	}

	public VerificationToken save(Token token) {
		VerificationToken verificationToken = (VerificationToken) token;
		return repository.save(verificationToken);
	}

}

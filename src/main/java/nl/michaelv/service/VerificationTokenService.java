package nl.michaelv.service;

import nl.michaelv.model.Token;
import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;
import nl.michaelv.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("verificationTokenService")
public class VerificationTokenService implements TokenService {

	@Autowired
	private VerificationTokenRepository repository;

	public VerificationToken findByToken(String token) {
		return this.repository.findByToken(token);
	}

	public List<VerificationToken> findByUser(User user) {
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

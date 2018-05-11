package nl.michaelv.service;

import javax.validation.Valid;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.PasswordToken;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.repository.PasswordTokenRepository;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("passwordTokenService")
public class PasswordTokenService implements TokenService {

	@Autowired
	private PasswordTokenRepository repository;

	public Token findByToken(@NotEmpty String token) {
		return this.repository.findByToken(token);
	}

	public List<Token> findByUser(@Valid User user) {
		return this.repository.findByUser(user);
	}

	public PasswordToken create(@Valid User user) {
		PasswordToken passwordToken = new PasswordToken(user);
		return repository.save(passwordToken);
	}

	public PasswordToken save(@NotEmpty Token token) {
		PasswordToken passwordToken = (PasswordToken) token;
		return repository.save(passwordToken);
	}

}

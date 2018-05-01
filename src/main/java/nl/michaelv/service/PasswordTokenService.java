package nl.michaelv.service;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.Token;
import nl.michaelv.model.User;
import nl.michaelv.repository.PasswordTokenRepository;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service("passwordTokenService")
public class PasswordTokenService implements TokenService {

	@Autowired
	private PasswordTokenRepository repository;

	public PasswordToken findByToken(@NotEmpty String token) {
		return this.repository.findByToken(token);
	}

	public List<PasswordToken> findByUser(@Valid User user) {
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

package nl.michaelv.service;

import nl.michaelv.model.Token;
import nl.michaelv.model.User;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.List;

public interface TokenService {

	public Token findByToken(@NotBlank String token);

	public List<? extends Token> findByUser(@Valid User user);

	public Token create(@Valid User user);

	// TODO: Shouldn't this be a generic so it takes as VerificationToken as a parameter?
	public Token save(@Valid Token token);

}

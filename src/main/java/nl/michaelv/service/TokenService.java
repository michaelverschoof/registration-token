package nl.michaelv.service;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.Token;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.List;

public interface TokenService {

	public Token findByToken(@NotBlank String token);

	public List<Token> findByUser(@Valid User user);

	public Token create(@Valid User user);

	public Token save(@Valid Token abstractToken);

}

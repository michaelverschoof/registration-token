package nl.michaelv.service;

import nl.michaelv.model.Token;
import nl.michaelv.model.User;

import java.util.List;

public interface TokenService {

	public Token findByToken(String token);

	public List<? extends Token> findByUser(User user);

	public Token create(User user);

	// TODO: Shouldn't this be a generic so it takes as VerificationToken as a parameter?
	public Token save(Token token);

}

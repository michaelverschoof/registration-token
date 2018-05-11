package nl.michaelv.model.tokens;

import nl.michaelv.model.User;

public interface Token {

	String token();

	User user();

	void confirm();

	boolean confirmed();

	boolean expired();

}

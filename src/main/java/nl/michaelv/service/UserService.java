package nl.michaelv.service;

import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;

public interface UserService {

	boolean exists(String email);

	User find(String email);

	User create(SignupForm form);

	User save(User user);

	void delete(User user);

	User changePassword(User user);

}

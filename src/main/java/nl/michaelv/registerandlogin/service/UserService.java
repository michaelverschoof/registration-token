package nl.michaelv.registerandlogin.service;

import nl.michaelv.registerandlogin.model.User;

public interface UserService {

	public User findUserByEmail(String email);

	public void saveUser(User user);
}

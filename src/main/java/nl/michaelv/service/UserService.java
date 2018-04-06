package nl.michaelv.service;

import nl.michaelv.model.User;

public interface UserService {

	public User findUserByEmail(String email);

	public void saveUser(User user);
}

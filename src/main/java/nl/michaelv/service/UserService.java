package nl.michaelv.service;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;

public interface UserService {

	public User findUserByEmail(String email);

	public User findUserByVerificationToken(String token);

	public User findUserByPasswordToken(String token);

	public User createUser(User user, String token);

	public User verifyUser(User user, VerificationToken token);

	public User alterPassword(User user, PasswordToken token);

	public User saveUser(User user);

	public VerificationToken findVerificationToken(String token);

	public VerificationToken createVerificationToken(User user, String token);

	public PasswordToken findPasswordToken(String token);

	public PasswordToken findPasswordTokenByUser(User user);

	public PasswordToken createPasswordToken(User user, String token);

}

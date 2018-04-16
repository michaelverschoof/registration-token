package nl.michaelv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

	PasswordToken findByToken(String token);

	PasswordToken findByUser(User user);
}

package nl.michaelv.repository;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

	PasswordToken findByToken(String token);

	List<PasswordToken> findByUser(User user);
}

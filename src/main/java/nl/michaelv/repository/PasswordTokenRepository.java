package nl.michaelv.repository;

import nl.michaelv.model.tokens.PasswordToken;
import nl.michaelv.model.User;
import nl.michaelv.model.tokens.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {

	Token findByToken(String token);

	List<Token> findByUser(User user);
}

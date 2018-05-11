package nl.michaelv.repository;

import nl.michaelv.model.User;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.model.tokens.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Token findByToken(String token);

	List<Token> findByUser(User user);
}

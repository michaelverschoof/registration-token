package nl.michaelv.repository;

import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	VerificationToken findByToken(String token);

	List<VerificationToken> findByUser(User user);
}

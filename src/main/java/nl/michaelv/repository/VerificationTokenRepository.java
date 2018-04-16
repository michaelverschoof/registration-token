package nl.michaelv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	VerificationToken findByToken(String token);

	VerificationToken findByUser(User user);
}

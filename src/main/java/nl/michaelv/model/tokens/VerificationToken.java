package nl.michaelv.model.tokens;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import nl.michaelv.model.User;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "verification_token")
public class VerificationToken extends AbstractToken {

	public VerificationToken(User user) {
		super(user);
	}

	public VerificationToken(String token, User user) {
		super(token, user);
	}
}

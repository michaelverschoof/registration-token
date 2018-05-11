package nl.michaelv.model.tokens;

import nl.michaelv.model.User;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

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

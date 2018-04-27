package nl.michaelv.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "verification_token")
public class VerificationToken extends Token {

	public VerificationToken(User user) {
		super(user);
	}

}

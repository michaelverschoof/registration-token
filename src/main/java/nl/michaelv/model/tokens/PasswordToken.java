package nl.michaelv.model.tokens;

import nl.michaelv.model.User;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "password_token")
public class PasswordToken extends AbstractToken {

	public PasswordToken(User user) {
		super(user);
	}

	public PasswordToken(String token, User user) {
		super(token, user);
	}

}

package nl.michaelv.model;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "password_token")
public class PasswordToken extends Token {

	public PasswordToken() {
		super();
	}

	public PasswordToken(String token, User user) {
		super(token, user);
	}

}

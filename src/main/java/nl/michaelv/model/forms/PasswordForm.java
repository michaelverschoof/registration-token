package nl.michaelv.model.forms;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class PasswordForm {

	@NotBlank(message = "Your password needs to contain some characters")
	@Min(value = 8, message = "Your password should at least be 8 characters long")
	@Max(value = 150, message = "Your password should not be longer than 150 characters")
	private String password;

	@NotBlank(message = "Your password confirmation needs to contain some characters")
	@Min(value = 8, message = "Your password confirmation should at least be 8 characters long")
	@Max(value = 150, message = "Your password confirmation should not be longer than 150 characters")
	private String passwordConfirmation;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

}
package nl.michaelv.model.forms;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignupForm extends PasswordForm {

	@NotBlank(message = "Your first name needs to contain some characters")
	@Max(value = 50, message = "Your first name should not be longer than 50 characters")
	private String firstName;

	@Max(value = 25, message = "Your middle name should not be longer than 25 characters")
	private String middleName;

	@NotBlank(message = "Your last name needs to contain some characters")
	@Max(value = 50, message = "Your last name should not be longer than 50 characters")
	private String lastName;

	@NotBlank(message = "Your email needs to contain some characters")
	@Max(value = 150, message = "Your email address should not be longer than 150 characters")
	@Email(message = "Please provide a valid email address")
	private String email;

	@Max(value = 50, message = "Your phone number should not be longer than 50 characters")
	@Pattern(regexp = "^\\(?(\\+?\\d{0,3})\\)?( |-)?\\d{7,10}$")
	private String phone;

}

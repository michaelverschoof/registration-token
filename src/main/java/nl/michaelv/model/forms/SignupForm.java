package nl.michaelv.model.forms;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class SignupForm extends PasswordForm {

	@NotBlank(message = "{signupform.name.first.blank}")
	@Max(value = 50, message = "{signupform.name.first.max}")
	private String firstName;

	@Max(value = 25, message = "{signupform.name.middle.max}")
	private String middleName;

	@NotBlank(message = "{signupform.name.last.blank}")
	@Max(value = 50, message = "{signupform.name.last.max}")
	private String lastName;

	@NotBlank(message = "{signupform.email.blank}")
	@Max(value = 150, message = "{signupform.email.max}")
	@Email(message = "{signupform.email.valid}")
	private String email;

	@Max(value = 50, message = "{signupform.phone.max}")
	@Pattern(regexp = "^\\(?(\\+?\\d{0,3})\\)?( |-)?\\d{7,10}$", message = "{signupform.phone.valid}")
	private String phone;

}

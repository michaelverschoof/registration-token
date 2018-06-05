package nl.michaelv.model.forms;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class SignupForm extends PasswordForm {

	@NotBlank(message = "{signupform.name.first.blank}")
	@Size(max = 50, message = "{signupform.name.first.max}")
	private String firstName;

	@Size(max = 25, message = "{signupform.name.middle.max}")
	private String middleName;

	@NotBlank(message = "{signupform.name.last.blank}")
	@Size(max = 50, message = "{signupform.name.last.max}")
	private String lastName;

	@NotBlank(message = "{signupform.email.blank}")
	@Size(max = 150, message = "{signupform.email.max}")
	@Email(message = "{signupform.email.valid}")
	private String email;

	@Size(max = 50, message = "{signupform.phone.max}")
	@Pattern(regexp = "^\\(?(\\+?\\d{0,3})\\)?( |-)?\\d{7,10}$", message = "{signupform.phone.valid}")
	private String phone;

}

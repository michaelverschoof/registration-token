package nl.michaelv.model.forms;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class ForgotPasswordForm {

	@NotBlank(message = "{signupform.email.blank}")
	@Size(max = 150, message = "{signupform.email.max}")
	@Email(message = "{signupform.email.valid}")
	private String email;

}

package nl.michaelv.model.forms;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class PasswordForm {

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 150;		// TODO: Useful number for max

	@NotBlank(message = "{passwordform.password.blank}")
	@Min(value = MIN_LENGTH, message = "{passwordform.password.min}")
	@Max(value = MAX_LENGTH, message = "{passwordform.password.max}")
	private String password;

	@NotBlank(message = "{passwordform.confirm.blank}")
	@Min(value = MIN_LENGTH, message = "{passwordform.confirm.min}")
	@Max(value = MAX_LENGTH, message = "{passwordform.confirm.max}")
	private String passwordConfirmation;

}

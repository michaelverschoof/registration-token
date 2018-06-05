package nl.michaelv.model.forms;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

@Getter
@Setter
public class PasswordForm {

	private static final int MIN_LENGTH = 8;
	private static final int MAX_LENGTH = 150;		// TODO: Useful number for max

	@NotBlank(message = "{passwordform.password.blank}")
	@Size(min = MIN_LENGTH, max = MAX_LENGTH, message = "{passwordform.password.size}")
	private String password;

	@NotBlank(message = "{passwordform.confirm.blank}")
	@Size(min = MIN_LENGTH, max = MAX_LENGTH, message = "{passwordform.confirm.size}")
	private String passwordConfirmation;

}

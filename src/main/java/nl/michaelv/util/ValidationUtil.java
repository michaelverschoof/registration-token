package nl.michaelv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

	@Autowired
	private static MessageUtil messages;

	private static final Pattern CONTAINS_NUMERIC = Pattern.compile("(?=.*[0-9])");
	private static final Pattern CONTAINS_LOWERCASE = Pattern.compile("(?=.*[a-z])");
	private static final Pattern CONTAINS_UPPERCASE = Pattern.compile("(?=.*[A-Z])");
	private static final Pattern CONTAINS_SPECIAL = Pattern.compile("(?=.*[!@#$%^&+=*()_-])");
	private static final Pattern CONTAINS_WHITESPACE = Pattern.compile("(?=\\s+$)");

	private static final Pattern PHONE_NUMBER = Pattern.compile("^\\(?(\\+?\\d{0,3})\\)?( |-)?\\d{7,10}$");

	private static final int PASSWORD_LENGTH = 8;

	public static List<String> validatePassword(String password) {
		ArrayList<String> issues = new ArrayList<>();

		if (password == null || password.length() < PASSWORD_LENGTH) {
			issues.add(messages.get("validation.password.min", PASSWORD_LENGTH));
		}

		if (!CONTAINS_NUMERIC.matcher(password).find()) {
			issues.add(messages.get("validation.password.contain.number"));
		}

		if (!CONTAINS_LOWERCASE.matcher(password).find()) {
			issues.add(messages.get("validation.password.contain.lowercase"));
		}

		if (!CONTAINS_UPPERCASE.matcher(password).find()) {
			issues.add(messages.get("validation.password.contain.uppercase"));
		}

		if (!CONTAINS_SPECIAL.matcher(password).find()) {
			issues.add(messages.get("validation.password.contain.special"));
		}

		if (CONTAINS_WHITESPACE.matcher(password).find()) {
			issues.add(messages.get("validation.password.no.whitespace"));
		}

		return issues;
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		return PHONE_NUMBER.matcher(phoneNumber).matches();
	}
}

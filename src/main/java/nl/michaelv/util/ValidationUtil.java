package nl.michaelv.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationUtil {

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
			issues.add("The password must be at least " + PASSWORD_LENGTH + " characters long");
		}

		if (!CONTAINS_NUMERIC.matcher(password).find()) {
			issues.add("The password must contain at least one number");
		}

		if (!CONTAINS_LOWERCASE.matcher(password).find()) {
			issues.add("The password must contain at least one lowercase letter");
		}

		if (!CONTAINS_UPPERCASE.matcher(password).find()) {
			issues.add("The password must contain at least one uppercase letter");
		}

		if (!CONTAINS_SPECIAL.matcher(password).find()) {
			issues.add("The password must contain at least one special character");
		}

		if (CONTAINS_WHITESPACE.matcher(password).find()) {
			issues.add("The password must not contain any whitespace characters");
		}

		return issues;
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		return PHONE_NUMBER.matcher(phoneNumber).matches();
	}
}

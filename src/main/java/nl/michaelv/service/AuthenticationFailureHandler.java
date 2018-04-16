package nl.michaelv.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		setDefaultFailureUrl("/login.html?error=true");

		super.onAuthenticationFailure(request, response, exception);

		String error = "The email address and password do not match";

		if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
			error = "The user account is disabled";
		} else if (exception.getMessage().equalsIgnoreCase("User account has expired")) {
			error = "The user account is expired";
		}

		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, error);
	}
}

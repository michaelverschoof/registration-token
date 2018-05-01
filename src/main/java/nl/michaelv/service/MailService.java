package nl.michaelv.service;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public interface MailService {

	public void sendVerificationMail(@Email String recipient, @NotBlank String url);

	public void sendVerifiedMail(@Email String recipient);

	public void sendForgotPasswordMail(@Email String recipient, @NotBlank String url);

	public void sendForgotPasswordCompletedMail(@Email String recipient);

}

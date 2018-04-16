package nl.michaelv.service;

public interface MailService {

	public void sendVerificationMail(String recipient, String url);

	public void sendVerifiedMail(String recipient);

	public void sendForgotPasswordMail(String recipient, String url);

	public void sendForgotPasswordCompletedMail(String recipient);

}

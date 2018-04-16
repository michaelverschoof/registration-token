package nl.michaelv.service;

import org.springframework.stereotype.Service;

@Service("mailService")
public class MailServiceImpl implements MailService {

	// TODO: Enable mailing of the token
	// @Autowired
	// private JavaMailSender mailSender;

	public void sendVerificationMail(String recipient, String url) {
		String subject = "Email verification";
		String message = "Please verify your email address (" + recipient + ")." + "\n" + url;

		sendMail(recipient, subject, message);
	}

	public void sendVerifiedMail(String recipient) {
		String subject = "Email verified";
		String message = "Thank you for verifying your email address (" + recipient + ").";

		sendMail(recipient, subject, message);
	}

	public void sendForgotPasswordMail(String recipient, String url) {
		String subject = "Forgot password email verification";
		String message = "Please verify your email address (" + recipient + ") for your password reset." + "\n" + url;

		sendMail(recipient, subject, message);
	}

	public void sendForgotPasswordCompletedMail(String recipient) {
		String subject = "Forgot password completed";
		String message = "Your password has been altered. You can now log in using your new password.";

		sendMail(recipient, subject, message);
	}

	private void sendMail(String recipient, String subject, String message) {
		// TODO: Remove this when enabling mailing of the token
		System.out.println("Email:\nTo : " + recipient + ",\nSubject : " + subject + "\nMessage : " + message);

		// SimpleMailMessage email = new SimpleMailMessage();
		// email.setTo(recipient);
		// email.setSubject(subject);
		// email.setText(message);
		// mailSender.send(email);
	}
}

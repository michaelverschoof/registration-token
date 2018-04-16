package nl.michaelv.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;
import nl.michaelv.service.MailServiceImpl;
import nl.michaelv.service.UserService;
import nl.michaelv.util.ValidationUtil;

@Controller
public class SignUpController {

	@Autowired
	private UserService userService;

	@Autowired
	MailServiceImpl mailService;

	@GetMapping("/signup")
	public String signup(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("tab", "signup");
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid User user, BindingResult result, HttpServletRequest request, Model model) {
		model.addAttribute("tab", "signup");

		if (!result.hasErrors()) {
			User exists = userService.findUserByEmail(user.getEmail());
			if (exists != null) {
				result.rejectValue("email", null, "There is already a user registered with the email provided");
				return "signup";
			}

			if (!user.getPassword().equals(user.getPasswordConfirmation())) {
				result.rejectValue("password", null, "The passwords do not match");
				return "signup";
			}

			List<String> issues = ValidationUtil.validatePassword(user.getPassword());
			if (issues.size() > 0) {
				for (String issue : issues) {
					result.rejectValue("password", null, issue);
				}
				return "signup";
			}

			String token = UUID.randomUUID().toString();
			User created = userService.createUser(user, token);
			if (created == null) {
				result.reject(null, "The registration has failed for some reason...");
				return "signup";
			}

			String url = request.getRequestURI() + "/signup/verify/" + token;
			mailService.sendVerificationMail(created.getEmail(), url);

			model.addAttribute("message", "The registration has completed successfully. "
					+ "A verification email has been sent to " + created.getEmail());
			model.addAttribute("user", new User());
		}

		return "signup";
	}

	@GetMapping("/signup/verify/{token}")
	public String verifySignup(@PathVariable String token, final RedirectAttributes redirectAttributes) {
		VerificationToken verificationToken = userService.findVerificationToken(token);
		if (token == null) {
			redirectAttributes.addFlashAttribute("error", "The used token does not exist");
			return "redirect:/";
		}

		if (verificationToken.isUsed()) {
			redirectAttributes.addFlashAttribute("error", "This token has already been used");
			return "redirect:/";
		}

		if (verificationToken.getExpirationDate().isBefore(ZonedDateTime.now())) {
			redirectAttributes.addFlashAttribute("error", "This token has expired");
			return "redirect:/";
		}

		User user = verificationToken.getUser();
		if (user.isVerified()) {
			redirectAttributes.addFlashAttribute("error", "This user has already been verified");
			return "redirect:/";
		}

		User verified = userService.verifyUser(user, verificationToken);
		if (verified == null) {
			redirectAttributes.addFlashAttribute("error",
					"The verification of the email address has failed for some reason...");
			return "redirect:/";
		}

		mailService.sendVerifiedMail(verified.getEmail());

		redirectAttributes.addFlashAttribute("message", "Your email address has been verified and you can now log in");
		return "redirect:/";
	}

}

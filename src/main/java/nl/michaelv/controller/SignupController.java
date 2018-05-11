package nl.michaelv.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.service.MailService;
import nl.michaelv.service.TokenService;
import nl.michaelv.service.UserService;
import nl.michaelv.util.ValidationUtil;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {

	@Autowired
	private UserService userService;

	@Autowired
	private MailService mailService;

	@Autowired
	@Qualifier("verificationTokenService")
	private TokenService verificationTokenService;

	@GetMapping("/signup")
	public String signup(Model model) {
		SignupForm form = new SignupForm();
		model.addAttribute("form", form);
		model.addAttribute("tab", "signup");
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid SignupForm form, BindingResult result, HttpServletRequest request, Model model) {
		model.addAttribute("tab", "signup");

		if (!result.hasErrors()) {
			if (userService.exists(form.getEmail())) {
				result.rejectValue("email", null, "There is already a user registered with the email provided");
				return "signup";
			}

			List<String> issues = ValidationUtil.validatePassword(form.getPassword());
			if (issues.size() > 0) {
				for (String issue : issues) {
					result.rejectValue("password", null, issue);
				}
				return "signup";
			}

			if (!form.getPassword().equals(form.getPasswordConfirmation())) {
				result.rejectValue("password", null, "The passwords do not match");
				return "signup";
			}

			User user = userService.create(form);
			if (user == null) {
				result.reject(null, "The registration has failed for some reason...");
				return "signup";
			}

			Token verificationToken = verificationTokenService.create(user);
			if (verificationToken == null) {
				userService.delete(user);
				result.reject(null, "The registration has failed for some reason...");
				return "signup";
			}

			String url = request.getRequestURI() + "/verify/" + verificationToken.token();
			mailService.sendVerificationMail(user.getEmail(), url);

			model.addAttribute("message", "The registration has completed successfully. "
					+ "A verification email has been sent to " + user.getEmail());
			model.addAttribute("user", new User());
		}

		return "signup";
	}

	@GetMapping("/signup/verify/{token}")
	public String verifySignup(@PathVariable String token, final RedirectAttributes redirectAttributes) {
		Token verificationToken = verificationTokenService.findByToken(token);

		if (token == null) {
			redirectAttributes.addFlashAttribute("error", "The used token does not exist");
			return "redirect:/";
		}

		if (verificationToken.confirmed()) {
			redirectAttributes.addFlashAttribute("error", "This token has already been used");
			return "redirect:/";
		}

		if (verificationToken.expired()) {
			redirectAttributes.addFlashAttribute("error", "This token has expired");
			return "redirect:/";
		}

		User user = verificationToken.user();
		if (user.isVerified()) {
			redirectAttributes.addFlashAttribute("error", "This user has already been verified");
			return "redirect:/";
		}

		verificationToken.confirm();
		verificationTokenService.save(verificationToken);

		user.verify();
		User verified = userService.save(user);

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

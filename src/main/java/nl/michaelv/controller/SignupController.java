package nl.michaelv.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.service.MailService;
import nl.michaelv.service.TokenService;
import nl.michaelv.service.UserService;
import nl.michaelv.util.MessageUtil;
import nl.michaelv.util.ValidationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	@Autowired
	private MessageUtil messages;

	private static final String REDIRECT_HOME = "redirect:/";

	private static final String SIGNUP = "signup";

	private static final String ERROR = "error";

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("tab", SIGNUP);
		model.addAttribute("signupForm", new SignupForm());
		return SIGNUP;
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute SignupForm signupForm, BindingResult result, HttpServletRequest request, Model model) {
		model.addAttribute("tab", SIGNUP);
		model.addAttribute("signupForm", signupForm);

		if (!result.hasErrors()) {
			if (userService.exists(signupForm.getEmail())) {
				result.rejectValue("email", null, messages.get("validation.user.exists"));
				return SIGNUP;
			}

			List<String> issues = ValidationUtil.validatePassword(signupForm.getPassword());
			if (!issues.isEmpty()) {
				for (String issue : issues) {
					result.rejectValue("password", null, issue);
				}
				return SIGNUP;
			}

			if (!signupForm.getPassword().equals(signupForm.getPasswordConfirmation())) {
				result.rejectValue("passwordConfirmation", null, messages.get("validation.password.notequal"));
				return SIGNUP;
			}

			User user = userService.create(signupForm);
			if (user == null) {
				result.reject(null, messages.get("message.signup.fail"));
				return SIGNUP;
			}

			Token verificationToken = verificationTokenService.create(user);
			if (verificationToken == null) {
				userService.delete(user);
				result.reject(null, messages.get("message.signup.fail"));
				return SIGNUP;
			}

			String url = request.getRequestURI() + "/verify/" + verificationToken.token();
			mailService.sendVerificationMail(user.getEmail(), url);

			model.addAttribute("message", messages.get("message.signup.success", user.getEmail()));
			model.addAttribute("form", new SignupForm());
		}

		return SIGNUP;
	}

	@GetMapping("/signup/verify/{token}")
	public String verifySignup(@PathVariable String token, final RedirectAttributes redirectAttributes) {
		Token verificationToken = verificationTokenService.findByToken(token);

		if (token == null) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.notfound"));
			return REDIRECT_HOME;
		}

		if (verificationToken.confirmed()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.used"));
			return REDIRECT_HOME;
		}

		if (verificationToken.expired()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.expired"));
			return REDIRECT_HOME;
		}

		User user = verificationToken.user();
		if (user.isVerified()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.verified"));
			return REDIRECT_HOME;
		}

		verificationToken.confirm();
		verificationTokenService.save(verificationToken);

		user.verify();
		User verified = userService.save(user);

		if (verified == null) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("message.signup.verification.fail"));
			return REDIRECT_HOME;
		}

		mailService.sendVerifiedMail(verified.getEmail());

		redirectAttributes.addFlashAttribute("message", messages.get("message.signup.verification.success"));
		return REDIRECT_HOME;
	}

}

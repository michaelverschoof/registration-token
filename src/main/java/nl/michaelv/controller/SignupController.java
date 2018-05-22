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
	private static MessageUtil messages;

	@GetMapping("/signup")
	public String signup(Model model) {
		SignupForm form = new SignupForm();
		model.addAttribute("form", form);
		model.addAttribute("tab", "signup");
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute SignupForm form, BindingResult result, HttpServletRequest request, Model model) {
		model.addAttribute("tab", "signup");
		model.addAttribute("form", form);

		if (!result.hasErrors()) {
			if (userService.exists(form.getEmail())) {
				result.rejectValue("email", null, messages.get("validation.user.exists"));
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
				result.rejectValue("passwordConfirmation", null, messages.get("validation.password.notequal"));
				return "signup";
			}

			User user = userService.create(form);
			if (user == null) {
				result.reject(null, messages.get("message.signup.fail"));
				return "signup";
			}

			Token verificationToken = verificationTokenService.create(user);
			if (verificationToken == null) {
				userService.delete(user);
				result.reject(null, messages.get("message.signup.fail"));
				return "signup";
			}

			String url = request.getRequestURI() + "/verify/" + verificationToken.token();
			mailService.sendVerificationMail(user.getEmail(), url);

			model.addAttribute("message", messages.get("message.signup.success", user.getEmail()));
			model.addAttribute("form", new SignupForm());
		}

		return "signup";
	}

	@GetMapping("/signup/verify/{token}")
	public String verifySignup(@PathVariable String token, final RedirectAttributes redirectAttributes) {
		Token verificationToken = verificationTokenService.findByToken(token);

		if (token == null) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.notfound"));
			return "redirect:/";
		}

		if (verificationToken.confirmed()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.used"));
			return "redirect:/";
		}

		if (verificationToken.expired()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.expired"));
			return "redirect:/";
		}

		User user = verificationToken.user();
		if (user.isVerified()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.verified"));
			return "redirect:/";
		}

		verificationToken.confirm();
		verificationTokenService.save(verificationToken);

		user.verify();
		User verified = userService.save(user);

		if (verified == null) {
			redirectAttributes.addFlashAttribute("error", messages.get("message.signup.verification.fail"));
			return "redirect:/";
		}

		mailService.sendVerifiedMail(verified.getEmail());

		redirectAttributes.addFlashAttribute("message", messages.get("message.signup.verification.success"));
		return "redirect:/";
	}

}

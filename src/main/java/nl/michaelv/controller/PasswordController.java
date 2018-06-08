package nl.michaelv.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import nl.michaelv.model.User;
import nl.michaelv.model.forms.PasswordForm;
import nl.michaelv.model.tokens.Token;
import nl.michaelv.service.TextualMailService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordController {

	@Autowired
	private UserService userService;

	@Autowired
	@Qualifier("passwordTokenService")
	private TokenService passwordTokenService;

	@Autowired
	private TextualMailService mailService;

	@Autowired
	private MessageUtil messages;

	private static final String LOGIN = "login";

	private static final String FORGOT = "forgot-password";

	private static final String SET = "set-password";

	private static final String REDIRECT_HOME = "redirect:/";

	private static final String ERROR = "error";

	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		model.addAttribute("tab", LOGIN);
		return FORGOT;
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam(name = "email", required = true) String email,
			HttpServletRequest request, Model model) {

		model.addAttribute("tab", LOGIN);

		// TODO: Forgot password form instead of an html basic form to enable validation and prepare for possible expansion
		if (email == null || email.trim().isEmpty()) {
			model.addAttribute(ERROR, messages.get("validation.email.empty"));
			return FORGOT;
		}

		User user = userService.find(email);
		if (user == null) {
			model.addAttribute(ERROR,
					messages.get("validation.user.notfound",
							email));
			return FORGOT;
		}

		Token passwordToken = passwordTokenService.create(user);
		if (passwordToken == null) {
			model.addAttribute(ERROR, messages.get("validation.token.notcreated"));
			return FORGOT;
		}

		String url = request.getRequestURI() + "/verify/" + passwordToken.token();
		mailService.sendForgotPasswordMail(user.getEmail(), url);

		model.addAttribute("message", messages.get("message.token.sent", user.getEmail()));

		return FORGOT;
	}

	@GetMapping("/forgot-password/verify/{token}")
	public String verifyForgotPassword(@PathVariable String token, RedirectAttributes redirectAttributes) {
		Token passwordToken = passwordTokenService.findByToken(token);
		if (passwordToken == null) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.notfound"));
			return REDIRECT_HOME;
		}

		if (passwordToken.confirmed()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.used"));
			return REDIRECT_HOME;
		}

		if (passwordToken.expired()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.token.expired"));
			return REDIRECT_HOME;
		}

		User user = passwordToken.user();
		if (!user.isVerified()) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.user.notverified"));
			return REDIRECT_HOME;
		}

		passwordToken.confirm();
		passwordTokenService.save(passwordToken);

		redirectAttributes.addFlashAttribute("user", user);
		return REDIRECT_HOME + SET;
	}

	@GetMapping("/set-password")
	public String changePassword(Model model, final RedirectAttributes redirectAttributes) {
		if (!model.containsAttribute("user")) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("validation.user.notinmodel"));
			return REDIRECT_HOME;
		}

		PasswordForm form = new PasswordForm();
		model.addAttribute("form", form);
		model.addAttribute("tab", LOGIN);
		return SET;
	}

	@PostMapping("/set-password")
	public String changePassword(@Valid @ModelAttribute PasswordForm passwordForm, User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (!passwordForm.getPassword().equals(passwordForm.getPasswordConfirmation())) {
			result.rejectValue("password", null, messages.get("validation.password.notequal"));
			return SET;
		}

		List<String> issues = ValidationUtil.validatePassword(user.getPassword());
		if (!issues.isEmpty()) {
			for (String issue : issues) {
				result.rejectValue("password", null, issue);
			}
			return SET;
		}

		user.setPassword(passwordForm.getPassword());

		User saved = userService.changePassword(user);
		if (saved == null) {
			redirectAttributes.addFlashAttribute(ERROR, messages.get("message.password.change.fail"));
			return REDIRECT_HOME;
		}

		mailService.sendForgotPasswordCompletedMail(saved.getEmail());
		redirectAttributes.addFlashAttribute("message", messages.get("message.password.change.success"));
		return REDIRECT_HOME;
	}

}

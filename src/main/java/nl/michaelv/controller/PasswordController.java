package nl.michaelv.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

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
import org.springframework.context.i18n.LocaleContextHolder;
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
	private static MessageUtil messages;

	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		model.addAttribute("tab", "login");
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam(name = "email", required = true) String email,
			HttpServletRequest request, Model model) {

		model.addAttribute("tab", "login");

		Locale locale = LocaleContextHolder.getLocale();

		// TODO: Forgot password form instead of an html basic form to enable validation and prepare for possible expansion
		if (email == null || email.trim().isEmpty()) {
			model.addAttribute("error", messages.get("validation.email.empty"));
			return "forgot-password";
		}

		User user = userService.find(email);
		if (user == null) {
			model.addAttribute("error", messages.get("validation.user.notfound", email));
			return "forgot-password";
		}

		Token passwordToken = passwordTokenService.create(user);
		if (passwordToken == null) {
			model.addAttribute("error", messages.get("validation.token.notcreated"));
			return "forgot-password";
		}

		String url = request.getRequestURI() + "/verify/" + passwordToken.token();
		mailService.sendForgotPasswordMail(user.getEmail(), url);

		model.addAttribute("message", messages.get("message.token.sent", user.getEmail()));

		return "forgot-password";
	}

	@GetMapping("/forgot-password/verify/{token}")
	public String verifyForgotPassword(@PathVariable String token, RedirectAttributes redirectAttributes) {
		Token passwordToken = passwordTokenService.findByToken(token);
		if (passwordToken == null) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.notfound"));
			return "redirect:/";
		}

		if (passwordToken.confirmed()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.used"));
			return "redirect:/";
		}

		if (passwordToken.expired()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.token.expired"));
			return "redirect:/";
		}

		User user = passwordToken.user();
		if (!user.isVerified()) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.user.notverified"));
			return "redirect:/";
		}

		passwordToken.confirm();
		passwordTokenService.save(passwordToken);

		redirectAttributes.addFlashAttribute("user", user);
		return "redirect:/set-password";
	}

	@GetMapping("/set-password")
	public String changePassword(Model model, final RedirectAttributes redirectAttributes) {
		if (!model.containsAttribute("user")) {
			redirectAttributes.addFlashAttribute("error", messages.get("validation.user.notinmodel"));
			return "redirect:/";
		}

		PasswordForm form = new PasswordForm();
		model.addAttribute("form", form);
		model.addAttribute("tab", "login");
		return "set-password";
	}

	@PostMapping("/set-password")
	public String changePassword(@Valid @ModelAttribute PasswordForm form, User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (!form.getPassword().equals(form.getPasswordConfirmation())) {
			result.rejectValue("password", null, messages.get("validation.password.notequal"));
			return "set-password";
		}

		List<String> issues = ValidationUtil.validatePassword(user.getPassword());
		if (issues.size() > 0) {
			for (String issue : issues) {
				result.rejectValue("password", null, issue);
			}
			return "set-password";
		}

		user.setPassword(form.getPassword());

		User saved = userService.changePassword(user);
		if (saved == null) {
			redirectAttributes.addFlashAttribute("error", messages.get("message.password.change.fail"));
			return "redirect:/";
		}

		mailService.sendForgotPasswordCompletedMail(saved.getEmail());
		redirectAttributes.addFlashAttribute("message", messages.get("message.password.change.success"));
		return "redirect:/";
	}

}

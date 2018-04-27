package nl.michaelv.controller;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;
import nl.michaelv.model.forms.PasswordForm;
import nl.michaelv.service.TextualMailService;
import nl.michaelv.service.TokenService;
import nl.michaelv.service.UserService;
import nl.michaelv.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class PasswordController {

	@Autowired
	private UserService userService;

	@Autowired
	@Qualifier("passwordTokenService")
	private TokenService passwordTokenService;

	@Autowired
	private TextualMailService mailService;

	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		model.addAttribute("tab", "login");
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam(name = "email", required = true) String email,
			HttpServletRequest request, Model model) {

		model.addAttribute("tab", "login");

		User user = userService.find(email);
		if (user == null) {
			model.addAttribute("error", "No user found with email address: " + email);
			return "forgot-password";
		}

		PasswordToken passwordToken = (PasswordToken) passwordTokenService.create(user);
		if (passwordToken == null) {
			model.addAttribute("error", "The password verification token could not be created");
			return "forgot-password";
		}

		String url = request.getRequestURI() + "/verify/" + passwordToken.getToken();
		mailService.sendForgotPasswordMail(user.getEmail(), url);
		model.addAttribute("message", "A forgot password link has been sent to: " + user.getEmail());

		return "forgot-password";
	}

	@GetMapping("/forgot-password/verify/{token}")
	public String verifyForgotPassword(@PathVariable String token, RedirectAttributes redirectAttributes) {
		PasswordToken passwordToken = (PasswordToken) passwordTokenService.findByToken(token);
		if (passwordToken == null) {
			redirectAttributes.addFlashAttribute("error", "The used token does not exist");
			return "redirect:/";
		}

		if (passwordToken.isConfirmed()) {
			redirectAttributes.addFlashAttribute("error", "This token has already been used");
			return "redirect:/";
		}

		if (passwordToken.getExpirationDate().isBefore(ZonedDateTime.now())) {
			redirectAttributes.addFlashAttribute("error", "This token has expired");
			return "redirect:/";
		}

		User user = passwordToken.getUser();
		if (!user.isVerified()) {
			redirectAttributes.addFlashAttribute("error", "This user is not yet verified");
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
			redirectAttributes.addFlashAttribute("error", "The user could not be obtained");
			return "redirect:/";
		}

		PasswordForm form = new PasswordForm();
		model.addAttribute("form", form);
		model.addAttribute("tab", "login");
		return "set-password";
	}

	@PostMapping("/set-password")
	public String changePassword(@Valid PasswordForm form, User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

		if (!form.getPassword().equals(form.getPasswordConfirmation())) {
			result.rejectValue("password", null, "The passwords do not match");
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
			redirectAttributes.addFlashAttribute("error", "The password change did not succeed for unknown reasons");
			return "redirect:/";
		}

		mailService.sendForgotPasswordCompletedMail(saved.getEmail());

		redirectAttributes.addFlashAttribute("message",
				"Your password has successfully been altered. You can now log in.");
		return "redirect:/";
	}

}

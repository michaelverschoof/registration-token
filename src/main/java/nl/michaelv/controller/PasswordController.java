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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.User;
import nl.michaelv.service.MailServiceImpl;
import nl.michaelv.service.UserService;
import nl.michaelv.util.ValidationUtil;

@Controller
public class PasswordController {

	@Autowired
	private UserService userService;

	@Autowired
	MailServiceImpl mailService;

	@GetMapping("/forgot-password")
	public String forgotPassword(Model model) {
		model.addAttribute("tab", "login");
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@RequestParam(name = "email", required = true) String email,
			HttpServletRequest request, Model model) {

		model.addAttribute("tab", "login");

		User user = userService.findUserByEmail(email);
		if (user == null) {
			model.addAttribute("error", "No user found with email address: " + email);
			return "forgot-password";
		}

		String token = UUID.randomUUID().toString();
		PasswordToken passwordToken = userService.createPasswordToken(user, token);
		if (passwordToken == null) {
			model.addAttribute("error", "The password verification token could not be created");
			return "forgot-password";
		}

		String url = request.getRequestURI() + "/forgot-password/verify/" + passwordToken.getToken();
		mailService.sendForgotPasswordMail(user.getEmail(), url);
		model.addAttribute("message", "A forgot password link has been sent to: " + user.getEmail());

		return "redirect:/";
	}

	@GetMapping("/forgot-password/verify/{token}")
	public String verifyForgotPassword(@PathVariable String token, final RedirectAttributes redirectAttributes) {
		PasswordToken passwordToken = userService.findPasswordToken(token);
		if (token == null) {
			redirectAttributes.addFlashAttribute("error", "The used token does not exist");
			return "redirect:/";
		}

		if (passwordToken.isUsed()) {
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

		redirectAttributes.addFlashAttribute("user", user);
		return "redirect:/alter-password";
	}

	@GetMapping("/alter-password")
	public String alterPassword(Model model, final RedirectAttributes redirectAttributes) {

		if (!redirectAttributes.containsAttribute("user")) {
			model.addAttribute("error", "The user could not be obtained");
			return "redirect:/";
		}

		User user = (User) redirectAttributes.asMap().get("user");
		model.addAttribute("user", user);
		model.addAttribute("tab", "login");
		return "alter-password";
	}

	@PostMapping("/alter-password")
	public String alterPassword(@Valid User user, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {

		if (!user.getPassword().equals(user.getPasswordConfirmation())) {
			result.rejectValue("password", null, "The passwords do not match");
			return "alter-password";
		}

		List<String> issues = ValidationUtil.validatePassword(user.getPassword());
		if (issues.size() > 0) {
			for (String issue : issues) {
				result.rejectValue("password", null, issue);
			}
			return "alter-password";
		}

		PasswordToken token = userService.findPasswordTokenByUser(user);
		User saved = userService.alterPassword(user, token);
		if (saved == null) {
			redirectAttributes.addFlashAttribute("error", "The password change did not succeed for unknown reasons");
			return "redirect:/";
		}

		mailService.sendForgotPasswordCompletedMail(saved.getEmail());

		redirectAttributes.addFlashAttribute("message",
				"Your password has succesfully been altered. You can now log in.");
		return "redirect:/";
	}

}

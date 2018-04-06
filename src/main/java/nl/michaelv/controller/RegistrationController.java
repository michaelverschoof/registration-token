package nl.michaelv.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import nl.michaelv.model.User;
import nl.michaelv.service.UserService;

@Controller
public class RegistrationController {

	@Autowired
	private UserService userService;

	@GetMapping("/registration")
	public String registration(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("tab", "registration");
		return "registration";
	}

	@PostMapping("/registration")
	public String registration(@Valid User user, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			User exists = userService.findUserByEmail(user.getEmail());

			if (exists != null) {
				result.rejectValue("email", "error.user", "There is already a user registered with the email provided");
			} else {
				userService.saveUser(user);
				model.addAttribute("message", "The registration has completed successfully");
				model.addAttribute("user", new User());
			}
		}

		model.addAttribute("tab", "registration");
		return "registration";
	}
}

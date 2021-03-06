package nl.michaelv.controller;

import nl.michaelv.model.User;
import nl.michaelv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;

	@GetMapping("/secured/")
	public String home(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.find(auth.getName());
		model.addAttribute("user", user);
		model.addAttribute("tab", "home");
		return "/secured/home";
	}

}

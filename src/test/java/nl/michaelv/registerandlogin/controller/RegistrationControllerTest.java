package nl.michaelv.registerandlogin.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import nl.michaelv.controller.SignUpController;
import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.service.UserService;

public class RegistrationControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	SignUpController registrationController;

	@Spy
	List<User> users = new ArrayList<User>();

	@Spy
	Model model;

	@Mock
	BindingResult result;

	@BeforeClass
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		users = getUsers();
	}

	@Test
	public void saveUser() {
		when(result.hasErrors()).thenReturn(false);
		when(userService.findUserByEmail(anyString())).thenReturn(null);
		doNothing().when(userService).saveUser(any(User.class));

		Assert.assertEquals(registrationController.registration(users.get(0), result, null, model), "registration");
		Assert.assertEquals(model.asMap().get("message"), "The registration has completed successfully");
	}

	public List<User> getUsers() {
		User u = new User();
		u.setId(1);
		u.setFirstName("User1");
		u.setMiddleName("von");
		u.setLastName("Lastname");
		u.setEmail("user1@someprovider.com");
		u.setPassword("123456");
		u.setEnabled(true);
		u.setRoles(getRoles());
		users.add(u);

		u = new User();
		u.setId(2);
		u.setFirstName("User2");
		u.setLastName("Othername");
		u.setEmail("user2@someprovider.com");
		u.setPassword("abcdef");
		u.setEnabled(true);
		u.setRoles(getRoles());
		users.add(u);

		return users;
	}

	public List<Role> getRoles() {
		Role r = new Role();
		r.setId(1);
		r.setName("USER");

		ArrayList<Role> roles = new ArrayList<Role>();
		roles.add(r);

		return roles;
	}
}

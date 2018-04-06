package nl.michaelv.registerandlogin.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import nl.michaelv.registerandlogin.model.Role;
import nl.michaelv.registerandlogin.model.User;
import nl.michaelv.registerandlogin.service.UserService;

public class RegistrationControllerTest {

	@Mock
	private UserService userService;

	@InjectMocks
	RegistrationController registrationController;

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

		Assert.assertEquals(registrationController.registration(users.get(0), result, model), "registration");
		Assert.assertEquals(model.asMap().get("message"), "The registration has completed successfully");
	}

	public List<User> getUsers() {
		User u = new User();
		u.setId(1);
		u.setName("User1");
		u.setEmail("user1@someprovider.com");
		u.setPassword("123456");
		u.setActive(true);
		u.setRoles(getRoles());
		users.add(u);

		u = new User();
		u.setId(2);
		u.setName("User2");
		u.setEmail("user2@someprovider.com");
		u.setPassword("abcdef");
		u.setActive(true);
		u.setRoles(getRoles());
		users.add(u);

		return users;
	}

	public Set<Role> getRoles() {
		Role r = new Role();
		r.setId(1);
		r.setName("USER");

		HashSet<Role> roles = new HashSet<Role>();
		roles.add(r);

		return roles;
	}
}

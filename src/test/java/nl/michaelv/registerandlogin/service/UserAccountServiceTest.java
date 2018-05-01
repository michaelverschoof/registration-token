package nl.michaelv.registerandlogin.service;

import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;
import nl.michaelv.repository.RoleRepository;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.service.UserAccountService;
import nl.michaelv.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserAccountServiceTest {

	@TestConfiguration
	static class UserServiceImplTestContextConfiguration {
		@Bean
		public UserService userService() {
			return new UserAccountService();
		}
	}

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RoleRepository roleRepository;

	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private User user;

	@Before
	public void before() {
		user = getUser();
		when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
		when(userRepository.save(any(User.class))).thenReturn(user);
		when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

		Role role = getRole();
		when(roleRepository.findByName("USER")).thenReturn(role);
	}

	@Test
	public void findByEmail() {
		User found = userService.find("user1@someprovider.com");
		assertNotNull(found);
		assertThat(found.getFirstName()).isEqualTo("User1");
		assertThat(hasRole(found, "USER")).isEqualTo(true);
	}

	@Test
	public void findByEmailWithUnknownValue() {
		User found = userService.find("other@someprovider.com");
		assertNull(found);
	}

	@Test
	public void existsByEmail() {
		boolean found = userService.exists("user1@someprovider.com");
		assertThat(found).isTrue();
	}

	@Test
	public void existsByEmailWithUnknownValue() {
		boolean found = userService.exists("other@someprovider.com");
		assertThat(found).isFalse();
	}

	@Test
	public void createFromForm() {
		User saved = userService.create(getSignupForm());
		assertNotNull(saved);
		assertThat(saved.getFirstName()).isEqualTo("User1");
		assertThat(hasRole(saved, "USER")).isEqualTo(true);
	}

	@Test
	public void createFromFormWithInvalidValue() {
//		SignupForm form = getSignupForm();
//		form.setEmail("");
//		User saved = userService.create(form);
//		assertNull(saved);
	}


	private User getUser() {
		User u = new User();
		u.setId(1);
		u.setFirstName("User1");
		u.setMiddleName("von");
		u.setLastName("Lastname");
		u.setEmail("user1@someprovider.com");
		u.setPassword("123456");
		u.setVerified(true);
		u.addRole(getRole());

		return u;
	}

	private SignupForm getSignupForm() {
		SignupForm form = new SignupForm();
		form.setFirstName("User1");
		form.setMiddleName("von");
		form.setLastName("Lastname");
		form.setEmail("user1@someprovider.com");
		form.setPassword("123456");
		form.setPasswordConfirmation("123456");

		return form;
	}

	private Role getRole() {
		Role r = new Role();
		r.setId(1);
		r.setName("USER");

		return r;
	}

	public boolean hasRole(User user, String name) {
		List<Role> roles = user.getRoles();

		for (Role role : roles) {
			if (role.getName().equals(name)) {
				return true;
			}
		}

		return false;
	}
}

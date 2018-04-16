package nl.michaelv.registerandlogin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.repository.RoleRepository;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.service.UserService;
import nl.michaelv.service.UserServiceImpl;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

	@TestConfiguration
	static class UserServiceImplTestContextConfiguration {

		@Bean
		public UserService userService() {
			return new UserServiceImpl();
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

	@Before
	public void setUp() {
		User user = getUser();
		when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
		when(userRepository.save(user)).thenReturn(user);

		Role role = getRole();
		when(roleRepository.findByName("USER")).thenReturn(role);
	}

	@Test
	public void findUserByEmail() {
		String email = "user1@someprovider.com";
		User found = userService.findUserByEmail(email);

		assertThat(found.getEmail()).isEqualTo(email);
		assertThat(hasRole(found, "USER")).isEqualTo(true);
	}

	@Test
	public void findUserByEmail_NotFound() {
		String email = "notexisting@someprovider.com";
		User found = userService.findUserByEmail(email);

		assertThat(found).isEqualTo(null);
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

package nl.michaelv.service;

import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.model.forms.SignupForm;
import nl.michaelv.repository.RoleRepository;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.util.RoleUtil;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service("userService")
public class UserAccountService implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String DEFAULT_ROLE = "USER";

	public User find(String email) {
		return userRepository.findByEmail(email);
	}

	public boolean exists(String email) {
		return userRepository.existsByEmail(email);
	}

	public User create(SignupForm form) {
		User user = new User();
		user.setFirstName(form.getFirstName());
		user.setMiddleName(form.getMiddleName());
		user.setLastName(form.getLastName());
		user.setEmail(form.getEmail());
		user.setPhone(form.getPhone());
		user.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
		user.setVerified(false);
		user.addRole(getDefaultRole());

		return userRepository.save(user);
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public void delete(User user) {
		userRepository.delete(user);
	}

	public User changePassword(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		return save(user);
	}

	public UserDetails loadUserDetailsByUsername(String email) throws UsernameNotFoundException {
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		try {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				throw new UsernameNotFoundException("No user found with email: " + email);
			}

			return new org.springframework.security.core.userdetails.User(user.getEmail(),
					user.getPassword().toLowerCase(), user.isVerified(), accountNonExpired, credentialsNonExpired,
					accountNonLocked, RoleUtil.getAuthorities(user.getRoles()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Role getDefaultRole() {
		Role role = roleRepository.findByName(DEFAULT_ROLE);
		if (role == null) {
			role = new Role();
			role.setName(DEFAULT_ROLE);
			return roleRepository.save(role);
		}
		return role;
	}
}

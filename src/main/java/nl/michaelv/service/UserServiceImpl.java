package nl.michaelv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import nl.michaelv.model.PasswordToken;
import nl.michaelv.model.Role;
import nl.michaelv.model.User;
import nl.michaelv.model.VerificationToken;
import nl.michaelv.repository.PasswordTokenRepository;
import nl.michaelv.repository.RoleRepository;
import nl.michaelv.repository.UserRepository;
import nl.michaelv.repository.VerificationTokenRepository;
import nl.michaelv.util.RoleUtil;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private PasswordTokenRepository passwordTokenRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final String DEFAULT_ROLE = "USER";

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findUserByVerificationToken(String token) {
		User user = verificationTokenRepository.findByToken(token).getUser();
		return user;
	}

	public User findUserByPasswordToken(String token) {
		User user = passwordTokenRepository.findByToken(token).getUser();
		return user;
	}

	public User createUser(User user, String token) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setVerified(false);
		user.addRole(getDefaultRole());

		User saved = saveUser(user);
		if (saved != null) {
			VerificationToken verificationToken = createVerificationToken(user, token);
			if (verificationToken == null) {
				userRepository.delete(user);
				return null;
			}
		}

		return saved;
	}

	public User verifyUser(User user, VerificationToken token) {
		token.setUsed(true);
		verificationTokenRepository.save(token);

		user.setVerified(true);
		return saveUser(user);
	}

	public User alterPassword(User user, PasswordToken token) {
		token.setUsed(true);
		passwordTokenRepository.save(token);

		return saveUser(user);
	}

	private User saveUser(User user) {
		return userRepository.save(user);
	}

	public VerificationToken findVerificationToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}

	public VerificationToken createVerificationToken(User user, String token) {
		VerificationToken verificationToken = new VerificationToken(token, user);
		return verificationTokenRepository.save(verificationToken);
	}

	public PasswordToken findPasswordToken(String token) {
		return passwordTokenRepository.findByToken(token);
	}

	public PasswordToken findPasswordTokenByUser(User user) {
		return passwordTokenRepository.findByUser(user);
	}

	public PasswordToken createPasswordToken(User user, String token) {
		PasswordToken passwordToken = new PasswordToken(token, user);
		return passwordTokenRepository.save(passwordToken);
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

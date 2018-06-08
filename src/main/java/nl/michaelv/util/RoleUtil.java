package nl.michaelv.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.michaelv.model.Role;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleUtil {

	public static Collection<GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return authorities;
	}
}

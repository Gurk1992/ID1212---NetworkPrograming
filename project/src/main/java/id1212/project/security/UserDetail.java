package id1212.project.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import id1212.project.domain.User;

public class UserDetail implements UserDetails {
	private static final long serialVersionUID = 1L; 
	private Collection<? extends GrantedAuthority> authorities; 
	private String password; 
	private String username; 
	
	public  UserDetail(User user) { 
	
		
		this.username = user.getUsername(); 
		this.password = user.getPassword(); 
		
		this.authorities = translate(user.getRole()); 
	} 
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities; 
	}

	@Override
	public String getPassword() {
		return password; 
	}

	@Override
	public String getUsername() {
		return username; 
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; 
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; 
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; 
	}

	@Override
	public boolean isEnabled() {
		return true; 
	}
	
	private Collection<? extends GrantedAuthority> translate(String role) { 
		List<GrantedAuthority> authorities = new ArrayList<>(); 
			String name = role.toUpperCase(); 
			if (!name.startsWith("ROLE_")) { 
				name = "ROLE_" + name; 
			} 
			authorities.add(new SimpleGrantedAuthority(name)); 
		return authorities; 
	} 

}

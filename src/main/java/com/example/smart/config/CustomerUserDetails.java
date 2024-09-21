package com.example.smart.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.smart.entities.User;

/* Spring Security uses UserDetails to get user information such as username, 
 * password, and authorities (roles/permissions) required for authentication and authorization.
 * 
 * */

public class CustomerUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* WE ARE NOT USING @AUTOWIRED HERE BECOS
	 * CustomerUserDetails is not a Spring-managed bean. It is created manually in the loadUserByUsername method of CustomUserDetailsService.
	 * */
	private User u;
	
		public CustomerUserDetails(User u) {
		super();
		this.u = u;
       	}

		@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		SimpleGrantedAuthority sg=new SimpleGrantedAuthority(u.getRole());
		/* The SimpleGrantedAuthority class is used in Spring Security to represent a granted authority.
		 * An authority can be a role or a permission assigned to the user. 
		*/
		
		return List.of(sg);
		
	}

	@Override
	public String getPassword() {
		
		return u.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return u.getEmail();  //WE GET EMAIL HERE BECAZ WE WANT EMAIL INSTEAD OF USERNAME TO LOGIN
	}

}

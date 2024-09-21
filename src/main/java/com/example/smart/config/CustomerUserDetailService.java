package com.example.smart.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.smart.dao.UserRepository;
import com.example.smart.entities.User;

/* When a user tries to authenticate, Spring Security calls the loadUserByUsername method of your UserDetailsService implementation.
This method fetches user details (from a database, for example) and returns a UserDetails object.
Spring Security then uses the information in the UserDetails object (like username, password, and roles) to verify the
user’s credentials and grant access based on the authorities.
*/

public class CustomerUserDetailService implements UserDetailsService{
	@Autowired
	private UserRepository ur;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User u=ur.getUserByUserName(username);
		if(u==null) {
			throw new UsernameNotFoundException("Could not find User!!");
		}
		CustomerUserDetails cu=new CustomerUserDetails(u);
		return cu;
	}

}

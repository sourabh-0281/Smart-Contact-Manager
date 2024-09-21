package com.example.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration /*This annotation indicates that the class has @Bean definitions, which Spring will process to generate Spring beans 
                to be managed in the Spring container.*/
@EnableWebSecurity/*This annotation enables Spring Security's web security support and provides the Spring MVC integration.*/
public class Myconfig{

	@Bean
	public UserDetailsService getUserDetailService() {
		//This method is used to load user-specific data, typically from a database or another persistent storage.
		return new CustomerUserDetailService();
	} 
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		/*This method returns a BCryptPasswordEncoder bean, which is a password encoder implementation that uses the BCrypt 
		strong hashing function.Password encoding is essential for securely storing passwords.*/
		return new BCryptPasswordEncoder();
		}
	
	@Bean
	/*Spring automatically injects these beans wherever they are required in the application. 
	 *For example, DaoAuthenticationProvider will be used by Spring Security to handle authentication.
	 **/
	public DaoAuthenticationProvider authenticationProvider() {
		/*DaoAuthenticationProvider is an authentication provider that uses a UserDetailsService to retrieve user details and a PasswordEncoder 
		 * to verify passwords 
		 */
		          DaoAuthenticationProvider dp=new DaoAuthenticationProvider();
		          dp.setUserDetailsService(getUserDetailService());
		          dp.setPasswordEncoder(passwordEncoder());
		          return dp;
	}
	
	//configure method 
	@Bean 
     public SecurityFilterChain  securityfilter(HttpSecurity http) throws Exception{
		
		/* Cross-Site Request Forgery (CSRF) is an attack that tricks the victim into submitting a malicious request
		 * 
		 * By default, Spring Security enables CSRF protection for all web applications. This is a good default and should only be disabled 
		 * in certain scenarios where CSRF protection is not needed
		 * */
    	 http.authorizeHttpRequests(
    			 e->e.requestMatchers("/user/**").hasRole("USER")  //we can also avoid this and use @PreAuthorize("hasRole('NORMAL')")
    			     .requestMatchers("/admin/**").hasRole("ADMIN")  //above the @getmapping(/index)
    			                                  //for custom role use .hasauthority
    			     .requestMatchers("/**").permitAll()         
    			     .anyRequest().authenticated()
    			      )
    	            .formLogin(
                       f->f.loginPage("/signin")
                           .loginProcessingUrl("/dologin")
                           .defaultSuccessUrl("/user/index")                           
    				  )
    	            .logout(
    	            	l->l.permitAll()	
    	            	)
    	            .csrf(
    	            	c->c.disable()	
    	            );
    	            
          /* Several methods to config the behavior of ofrm
           *   -loginPage()          -the custom login page
           *   -loginProcessingUrl() -the url to submit the username and password
           *   -defaultSuccessUrl()  -the landing page after successful login
           *   -failureUrl           -the landing page after an unsuccessful login
           * */

    	 
         return http.build();   	 
     }	 
}


package com.example.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.smart.dao.UserRepository;
import com.example.smart.entities.User;
import com.example.smart.helper.Messages;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder bpe;
	
	@Autowired
	private UserRepository ur;
	
   @GetMapping("/")
    public String home() {
	  return "home";
	}
   
   @GetMapping("/signup")
   public String signup(Model m) {
	   m.addAttribute("user", new User());
	  return "signup";
	}
   
   @PostMapping("/do_register")
   public String registerUser(@Valid @ModelAttribute("user")User u, BindingResult br,@RequestParam(value="agreement",defaultValue = "false")boolean a,
                  		     Model m,HttpSession session) {
	   
	try {
		   if(!a) {
			   System.out.println(a);	
			   throw new Exception("You have not agreed terms and Conditions!");
			   }
		   
			if(br.hasErrors()) {
				System.out.println(br.toString());
		         m.addAttribute("user",u);
				return "signup";
			}
		      u.setRole("ROLE_USER");  //It is a convention in Spring Security to prefix roles with "ROLE_"
		      u.setEnabled(true);      //if want to use something other then eg:normal then use .hasAuthority in Myconfig instead of .hasrole
		      u.setImageUrl("userprofile.png");
		      if(u.getAbout()!=null) {
		    	  String abt=u.getAbout().trim();
		    	  u.setAbout(abt);
		      }
		      u.setPassword(bpe.encode(u.getPassword()));
		      User res=ur.save(u); 
		      System.out.println(res); 
			   m.addAttribute("user",new User()); //FIELDS WITHOUT NAME
				session.setAttribute("message", new Messages("Successfully Registered!!","alert-success"));
				return "signup";
	}catch(Exception e) {
		e.printStackTrace();
		m.addAttribute("user",u);  //FIELDS WITH NAME
		session.setAttribute("message", new Messages(e.getMessage(),"alert-danger"));
		 return "signup";
	}  
  }
   
   @GetMapping("/signin")
   public String customLogin(Model m) {
	   m.addAttribute("tile","this is login  page");
	   return "signin";
   }
   
   
   //FOR SESSION
   @PostMapping("/clear-session-message")
   @ResponseBody
   public void clearSessionMessage(HttpSession session) {
       session.removeAttribute("message");
   }
}

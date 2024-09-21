package com.example.smart.controller;

import java.util.Random;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.smart.dao.UserRepository;
import com.example.smart.entities.User;
import com.example.smart.helper.Messages;
import com.example.smart.service.EmailService;


import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	//GENERATE RANDOM NUMBER
	Random r=new Random(100000);
	
	@Autowired
	private UserRepository userrepository;
	
	@Autowired
	private  EmailService emailservice;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	@GetMapping("/forgotpassword")
	public String forgotpassword() {
		return "password/forgotpassword";
	}
	
	//SENDING OTP
	@PostMapping("/sendotp")
	public String otp(@RequestParam("email")String email,HttpSession ses,Model m) {
       	
	 	//GENERATE RANDOM NUMBER
	    User userByUserName = userrepository.getUserByUserName(email);
	    if(userByUserName==null) {
			ses.setAttribute("message", new Messages("No user exist with this email id", "alert-danger"));
			return "redirect:/forgotpassword";	
		}
	    
		int otp = r.nextInt(999999);
		
		//SENDING OTP TO EMAIL
		String from="sourabhdummy7@gmail.com";
		String to=email;
		String text=""+"<div style='border:1px solid #e2e2e2; padding:20px;'>"
		                      +"<h3>"
		                      +"OTP is "
		                      +"<b>"
		                      +otp
		                      +"</b>"
		                      +"</h3>"
		                      +"</div>";
		if(emailservice.sendEmail(to, from, "OTP", text)) {
			    m.addAttribute("tocheckotp",otp );
			    m.addAttribute("mail",email);
			  	return "password/verifyotp";
		}
	  	ses.setAttribute("message", new Messages("Please Check your email id", "alert-danger"));
		return "password/forgotpassword";
	}
	
	
	//VERIFY OTP
	@PostMapping("/verify")
	 public String Verify(@RequestParam("hotp")String hotp,@RequestParam("otp")String otp,@RequestParam("mail")String email,HttpSession ses,Model m) {
		
		if(hotp.equals(otp)) {
			m.addAttribute("mail", email);
		return"password/changepassword";
		}
		 //fail
		ses.setAttribute("message", new Messages("Invalid OTP", "alert-danger"));
		return "password/verifyotp";
	}
	
	
	//CHANGEPASSWORD
	@PostMapping("/changepassword")
	public String changepassword(@RequestParam("npass") String newpassword,@RequestParam("mail")String email,HttpSession ses) {
		User userByUserName = userrepository.getUserByUserName(email);
		userByUserName.setPassword(bcryptPasswordEncoder.encode(newpassword) );
		userrepository.save(userByUserName);
		

		return "redirect:/signin?change=Password has Succesfully changed";
	}
	
}

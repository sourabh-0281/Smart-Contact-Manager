package com.example.smart.controller;

import org.springframework.data.domain.*;  
import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.smart.dao.ContactRepository;
import com.example.smart.dao.UserRepository;
import com.example.smart.entities.Contact;
import com.example.smart.entities.User;
import com.example.smart.helper.Messages;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserCon {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptpasswordencoder;

/*When @ModelAttribute is applied to a method, it indicates that the method should add one or more model attributes. This is typically 
 * used to add common data to the model that might be needed by multiple request handling methods.
 * 
 * When @ModelAttribute is applied to a method parameter, it indicates that the parameter should be retrieved from the model
 *  and populated with request data (usually form data).
* */
	
	
	
	
	// ADDING COMMON DATA
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);

	}
	

	// USER HOMEPAGE
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/userdashboard";
	}

	
	// ADDFORM
	@GetMapping("/addcontact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/addcontactform";
	}
	
	
	// PROCESSING ADD CONTACT FORM
	@PostMapping("/processcontact")
	public String processContact(@Valid @ModelAttribute Contact contact,BindingResult res , @RequestParam("proimage") MultipartFile file,
			Principal principal, Model model,HttpSession s) {
		
		try {
			   if(res.hasErrors()) {
				   model.addAttribute("contact", contact);
				   throw new Exception();
			   }
			  
      			if (file.isEmpty()) {	
				     contact.setImage("defaultcontact.png");

				  
		     	} else {
					// file the file to folder and update the name to contact
					contact.setImage(file.getOriginalFilename());
					File saveFile = new ClassPathResource("static/img").getFile();
			    	Files.copy(file.getInputStream(),   Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename())        , StandardCopyOption.REPLACE_EXISTING);
					System.out.println("Image is uploaded");
		 	     }
		      	     String name = principal.getName();
		  		    User user = this.userRepository.getUserByUserName(name);
					user.getL().add(contact);
					contact.setU(user);
					this.userRepository.save(user);
					// MESSAGE SUCCESS
				   	 s.setAttribute("message", new Messages("Your contact is added !! Add more..", "alert-success"));
					 model.addAttribute("contact", new Contact());
					return "normal/addcontactform";
	      	} catch (Exception e) {
					
					e.printStackTrace();
					//MESSAGE FAILURE
					s.setAttribute("message", new Messages("Some went wrong !! Try again..", "alert-danger"));
					 model.addAttribute("contact", contact);
					return "normal/addcontactform";
		}
	
	}

	
	
	//VIEW CONTACTS
		@GetMapping("/viewcontacts/{page}")
		public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
			
			m.addAttribute("title", "Show User Contacts");
			
			String userName = principal.getName();
	
			User user = this.userRepository.getUserByUserName(userName);
	
			// currentPage-page
			// Contact Per page - 5
			Pageable pageable = PageRequest.of(page,5);
	          
			Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
	
			m.addAttribute("contact", contacts);
			m.addAttribute("currentpage", page);
			m.addAttribute("totalpages", contacts.getTotalPages());
	
			return "normal/viewcontact";
		}

		
	// PARTICULAR CONTACT
	@GetMapping("/particularcontact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();


		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getU().getId()) {
			model.addAttribute("PartiContact", contact);
			model.addAttribute("title", contact.getName());
		}
		return "normal/particularcontact";
	}

	
	// DELETE CONTACT
	@GetMapping("/delete/{cid}")
	@Transactional
	/*automatically commits the transaction if the method completes successfully.
       If an exception occurs, Spring rolls back the transaction to its previous state, ensuring no partial changes are saved.*/
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,Principal principal) {
		
		    Contact contact = this.contactRepository.findById(cId).get();        
			//setting null because it is mapped with user also
			contact.setU(null);  					
             contactRepository.delete(contact);
       		session.setAttribute("message", new Messages("Contact deleted succesfully...", "alert-success"));
		return "redirect:/user/viewcontacts/0";
		
	}
	//FOR SESSION
	 @PostMapping("/clear-session-message")
	    @ResponseBody
	    public void clearSessionMessage(HttpSession session) {
	        session.removeAttribute("message");
	    }
	
	 
	 //UPDATE CONTACT
	 //WE ARE USING POSTMAPPING HERE SO THAT IT WLL NOT SHOW  ID  IN URL AS IT WAS EARLIERE AS ANYONE WAS ABLE TO VIEW ANOTHERS DATA  
	 @PostMapping("/updateform/{cid}")
	 public String updateform(@PathVariable("cid")int cid,Model m) {
		   Contact c = contactRepository.findById(cid).get();
		   m.addAttribute("contact", c);
		   return "normal/updateform";
	 }
	 
	 
	 
	 //PROCESSUPDATE
	 @PostMapping("/processupdate")
	 public String processupdate(@Valid @ModelAttribute Contact contact,BindingResult res , @RequestParam("proimage") MultipartFile file,
				Principal principal, Model model,HttpSession ses) {
	    	
		 try {
			    if(res.hasErrors()) {
			
				     throw new Exception();
			     }
			    
			    //old contact detail
			   Contact oldc = contactRepository.findById(contact.getId()).get();
			
			   if(!file.isEmpty()) {
				   
			     //delete old photo
				   String deletefile=new ClassPathResource("static/img").getFile().getAbsolutePath();
				   File file2=new File(deletefile, oldc.getImage());
				   file2.delete();
				   
				   //add new photo
				   String savefile=new ClassPathResource("static/img").getFile().getAbsolutePath();		
				   Files.copy(file.getInputStream() ,  Paths.get(savefile+File.separator+file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING   );
				   contact.setImage(file.getOriginalFilename());			
			   }else {
				   contact.setImage(oldc.getImage());    
			   }
			   
			   User u= userRepository.getUserByUserName(principal.getName());
			   contact.setU(u);			   			
			   contactRepository.save(contact);
			   ses.setAttribute("message", new Messages("Contact has been succesfully Updated", "alert-success"));
			
			   return "redirect:/user/particularcontact/"+contact.getId();
			 	
		} catch (Exception e) {
			model.addAttribute("message", new Messages("Some went wrong !! Try again..", "alert-danger"));
			 model.addAttribute("contact", contact);
			
			 return "normal/updateform";
		}
	 }
	 
	 
	 //YOURPROFILE
	 @GetMapping("/yourprofile")
	 public String yourprofile(Model m,@ModelAttribute User user1) {
		 m.addAttribute("user", user1);
		 return "normal/yourprofile";
	 }
	 
	 //UPDATE YORE PROFILE
	 @PostMapping("/updateuserform/{uid}")
	   public String  proupdate(@PathVariable("uid")int id,Model m) {
		 User u= userRepository.findById(id).get();
		 m.addAttribute("user", u);
		 m.addAttribute("oldm", u.getEmail());
		 return "normal/updateuserform";
	    } 
	 
	 @PostMapping("/processuserupdate")
	 public String processuserupdate(@Valid @ModelAttribute User user,BindingResult res,@RequestParam("userproimage")MultipartFile img
			 ,HttpSession s ,Principal p,@RequestParam("oldm")String oldmail) {
		 
		 try {
			    //GENERAL ERRORS 
				 if(res.hasErrors()) {
					 throw new Exception();
				 }
                //IMAGE HANDLING
				 User oldu=userRepository.findById(user.getId()).get();
				 
				 if(img.isEmpty()) {
					 user.setImageUrl(oldu.getImageUrl());
				 }
				 else {
					 
					 //delete
					 String deletefile= new ClassPathResource("static/img/").getFile().getAbsolutePath();
					 File f=new File(deletefile, oldu.getImageUrl());
					 f.delete();
					 
					 //add new
					 String newimg=new ClassPathResource("static/img").getFile().getAbsolutePath();
					 Files.copy(img.getInputStream(), Paths.get(newimg+File.separator+img.getOriginalFilename() ) , StandardCopyOption.REPLACE_EXISTING);
					 user.setImageUrl(img.getOriginalFilename());
				 }
				 
				userRepository.save(user);
				 s.setAttribute("message", new Messages("User data updated succesfully", "alert-success"));
				 
				// If email has changed, log the user out	
				//AS WE ARE USING EMAIL AS LOGIN WE NEED TO LOGOUT
				 if(!oldmail.equals(user.getEmail())) {
					 s.setAttribute("message", new Messages("Email has been changed ypu need to login again", "alert-success"));
					 return "redirect:/signin";
				 }
				 
				 return "redirect:/user/yourprofile";
				
		 }catch (Exception e) {
			 s.setAttribute("message", new Messages("Some went wrong !! Try again..", "alert-danger"));
			 return "normal/updateform";
		}
	 }
	 
	 //DELETE USER
	 @GetMapping("/deleteuser/{uid}")
	 @Transactional
	 public String deleteuser(@PathVariable("uid")int id,HttpSession session) {
		 User user = userRepository.findById(id).get();
		 System.out.println(user);
	      user.setL(null);
		 userRepository.delete(user);
		 session.setAttribute("message", new Messages("Contact deleted succesfully...", "alert-success"));
		 return "redirect:/signin";
	 }
	 
	//SETTINGS 
	 @GetMapping("/settings")
	 public String settings() {
		 return "normal/Settings";
	 }
	 
	 //PROCESS SETTINGS
	 @PostMapping("/processsettings")
	 @Transactional
	 public String processsettings(@RequestParam("oldp")String oldp,@RequestParam("newp")String newp,Principal p,HttpSession ses) {
  
		 String name = p.getName();
		 
		 User currentUser = userRepository.getUserByUserName(name);
		System.out.println(oldp+newp);
		System.out.println(bcryptpasswordencoder.matches(oldp, currentUser.getPassword()));
		
		 if(bcryptpasswordencoder.matches(oldp, currentUser.getPassword())) {
			 
			 currentUser.setPassword(bcryptpasswordencoder.encode(newp));
			 userRepository.save(currentUser);
			 ses.setAttribute("message", new Messages("Password is Successfully updated", "alert-success"));
			 return "redirect:/user/index" ;
		 }else {
			 ses.setAttribute("message", new Messages("Old password does not match", "alert-danger"));
			 return "redirect:/user/settings";
		    }
		}
	 
	 /** PAYMENT INTEGRATION **/
	 
	 //DONATE
	 //(1)
	 @GetMapping("/donate")
	 public String donate() {	 
		 return "normal/donate";
	 }
	 
	 //create order
	 @PostMapping("/createorder")
	 @ResponseBody
	 public String createordder(@RequestBody Map<String, Object>data) throws Exception{

		 //(2)
     	int amt =Integer.parseInt( (String) data.get("amount"));
     	RazorpayClient razorpayClient = new RazorpayClient("rzp_test_PKJksoyRMSaVq8", "rg4gYo7ucrrCb8Qz9hELtlg8");
     	
     	JSONObject orderRequest = new JSONObject();
     	orderRequest.put("amount",amt*100); //*100 as we take amt in paise
     	orderRequest.put("currency","INR");
     	orderRequest.put("receipt", "receipt#1");
     	
     	///creating new order
     	//(3)
     	Order order = razorpayClient.orders.create(orderRequest);
     	System.out.println(order);
     	//if you want u can save this order in database
     	
	 	return order.toString();
	 }
	 
}

package com.example.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.smart.dao.ContactRepository;
import com.example.smart.dao.UserRepository;
import com.example.smart.entities.Contact;
import com.example.smart.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	    private UserRepository ur;
	@Autowired
	 private ContactRepository cr;
	
	
	@GetMapping("/search/{query}")
      public ResponseEntity<List<Contact>> search(@PathVariable("query")String query,Principal p){
                  User userByUserName = ur.getUserByUserName(p.getName());	  
                  List<Contact> byNameContainingAndUser = cr.findByNameContainingAndU(query, userByUserName);
                  return ResponseEntity.ok(byNameContainingAndUser);
      }
}

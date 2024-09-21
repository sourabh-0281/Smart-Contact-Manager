package com.example.smart.dao;

import java.util.List; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smart.entities.Contact;
import com.example.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
        
	      //Pagination
	    //A page is subset of list of objects
	    @Query("from Contact as c where c.u.id=:userid")
	    public Page<Contact> findContactByUser(@Param("userid") int userid,Pageable pg);
	                                                                                        				//Contains info. 1)Current page  2)Contact per page
	    
	    //SEARCH FUNCTION
	    public List<Contact> findByNameContainingAndU(String name,User u);
}

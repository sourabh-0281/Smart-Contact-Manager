package com.example.smart.entities;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Contact {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@NotBlank(message = "Name cannot be blank")
    @Size(min=3,message = "username must be between 3-12 charaters")
	private String name;
	@NotBlank(message = "SecondName cannot be blank")
	private String Secondname;
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid Email")
	private String email;
	@NotBlank(message = "Please mention work")
	private String work;
	@Column(length = 10)
	@Size(min = 10,max = 10,message = "Please enter valid phone number")
	private String phone;
	private String image;
	
	@Column(length = 1000)
	@Size(min = 2,message = "minimum 2 words required")
	private String description;
	
	@ManyToOne
	@JsonIgnore
	private User u;
	
	
	public User getU() {
		return u;
	}
	public void setU(User u) {
		this.u = u;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSecondname() {
		return Secondname;
	}
	public void setSecondname(String secondname) {
		Secondname = secondname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Contact [id=" + id + ", name=" + name + ", Secondname=" + Secondname + ", email=" + email + ", work="
				+ work + ", phone=" + phone + ", image=" + image + ", description=" + description + ", u=" + u + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return id==((Contact)obj).getId();
	}
}

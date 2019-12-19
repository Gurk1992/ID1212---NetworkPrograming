package id1212.project.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;


import lombok.Data;

@Data
@Entity
public class Dog {
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private  int id;

	@NotNull(message =" Name cannot be null")
	@Size(min=1, message="name cannot be empty")
	private String name;
	@NotNull(message =" Age cannot be null")
	private int age;
	
	@Size(min=1, message="Race cannot be empty")
	private String race;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(referencedColumnName = "ownerId")
	private Owner owner;
	
	Dog(){}
	public Dog(String name, int age, String race) {
		this.name = name;
		this.age = age;
		this.race = race;
	}
	public Dog(String name, int age) {
		this.name = name;
		this.age = age;
	}	
	

}

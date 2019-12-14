package com.dogschool.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Dog {
	
	@Id 
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private  int id;
		
	private String name;
	private int age;
	private String race;
	

	@ManyToOne
	@JoinColumn()
	private Owner owner;
	
	
	public Dog(String name, int age, String race) {
		this.name = name;
		this.age = age;
		this.race = race;
	}	
}

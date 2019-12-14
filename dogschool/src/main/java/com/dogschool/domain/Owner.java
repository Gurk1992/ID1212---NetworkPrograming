package com.dogschool.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;




@Data
@EqualsAndHashCode(exclude = "dogs")
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Owner {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  int id;
	@NotNull
	private String surName;
	@NotNull
	private String lastName;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
	private Set<Dog> dogs;
	
	
	public Owner(String surName, String lastName, Dog...dogs){
		this.surName = surName;
		this.lastName =lastName;
		this.dogs =Stream.of(dogs).collect(Collectors.toSet());
		this.dogs.forEach(x->x.setOwner(this));
	}
	
	
}

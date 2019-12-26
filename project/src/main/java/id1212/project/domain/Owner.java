package id1212.project.domain;
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
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;




@Data
@EqualsAndHashCode(exclude = "dogs")
@Entity
@NoArgsConstructor
public class Owner {
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  int ownerId;
	
	@NotNull(message =" surname cannot be null")
	@NotEmpty(message ="surname can not be empty")
	private String surName;
	@NotNull(message =" lastname cannot be null")
	@NotEmpty(message ="lastname can not be empty")
	private String lastName;
	
	@JsonIgnoreProperties(value = {"age", "race"}, allowSetters=true )
	@JsonManagedReference
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private Set<Dog> dogs;
	
	public Owner(int id, String name, String lastname, Dog dogs) {
		this.ownerId = id;
	}
	public Owner(String surName, String lastName, Dog...dogs){
		this.surName = surName;
		this.lastName =lastName;
		this.dogs =Stream.of(dogs).collect(Collectors.toSet());
		this.dogs.forEach(x->x.setOwner(this));
	}
	public void addDog(Dog dog) {
        dog.setOwner(this);
        dogs.add(dog);
    }

    public void removeDog(Dog dog) {
    	dogs.remove(dog);
        dog.setOwner(null);
    }

	
}

package com.dogschool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.dogschool.domain.Dog;
import com.dogschool.domain.Owner;
import com.dogschool.repository.DogRepository;
import com.dogschool.repository.OwnerRepository;



@SpringBootApplication
public class DogschoolApplication {
	@Autowired
	private OwnerRepository ownerRep;
	@Component
	class DataSetup implements ApplicationRunner{
		@Override
		public void run(ApplicationArguments args) throws Exception{
			ownerRep.save(new Owner("Bilbo","Bagins", new Dog("tro",5, "Rotweiler")));
		    
			ownerRep.save(new Owner("Frodo","Baggins", new Dog("trev", 10, "dräggel"),new Dog("trov", 10, "dräggel")));
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(DogschoolApplication.class, args);
	}

}

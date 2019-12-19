 package id1212.project;

	import lombok.extern.slf4j.Slf4j;


import org.springframework.boot.CommandLineRunner;
	import org.springframework.context.annotation.Bean;
	import org.springframework.context.annotation.Configuration;

import id1212.project.application.DogService;
import id1212.project.domain.Dog;
	import id1212.project.domain.Owner;
	import id1212.project.domain.User;
	
	import id1212.project.repository.OwnerRepository;
import id1212.project.repository.UserRepository;

	@Configuration
	@Slf4j
	public class LoadDatabase {
		@Bean
		  CommandLineRunner initDatabase(OwnerRepository Ownerrep, DogService dogService, UserRepository userRep) {
		    return args -> {
		    	Ownerrep.save(new Owner("Bilbo","Bagins", new Dog("tro",5, "Rotweiler")));
		    	Ownerrep.save(new Owner("Frodo","Baggins", new Dog("trev", 10, "dräggel"),new Dog("sum", 1, "dräggel")));
		    	Ownerrep.save(new Owner("froo","Bagins", new Dog("trump", 20)));
		    	//userRep.save(new User("test", "123a", "user"));
		    	dogService.saveUser(new User("test", "123a", "USER"));
		    	dogService.saveUser(new User("testa", "123a","ADMIN"));
		    };
		  }
	}
package id1212.project.presentation;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import id1212.project.application.DogService;
import id1212.project.domain.Dog;




@RestController
public class DogController {
	@Autowired
	private DogResrouceAssembler assembler;
	@Autowired
	private DogService service;
	
	@GetMapping("/dogs")
	CollectionModel<EntityModel<Dog>> allDogs(){
		List<EntityModel<Dog>> dogs = service.findAllDogs();
		 return new CollectionModel<>(dogs, linkTo(methodOn(DogController.class).allDogs()).withSelfRel());
	}
	@PostMapping("/dogs")
	@ResponseStatus(HttpStatus.CREATED)
	EntityModel<?> newDog( @RequestBody @Valid Dog newDog) throws URISyntaxException{
		System.out.println("hit?");
		EntityModel<Dog> dog = service.saveDog(newDog);
		
		return dog;	
	}
	@GetMapping("/dogs/{id}")
	EntityModel<Dog> oneDog(@PathVariable int id) {
		Dog dog = service.findDog(id);
		
		return assembler.toModel(dog);
	}
	@PutMapping("/dogs/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	EntityModel<?> replaceDog(@Valid @RequestBody Dog newDog, @PathVariable int id) {
		EntityModel<Dog> resource = service.updateDog(id, newDog);
		return resource;	
	}
	
	@DeleteMapping("/dogs/{id}")
	ResponseEntity<?> deleteDog(@PathVariable int id) {
		service.deleteDog(id);
		return ResponseEntity.noContent().build();
	}
}

package id1212.project.presentation;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import id1212.project.domain.Owner;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;


@RestController
public class OwnerController {
	
	@Autowired
	private  OwnerResourceAssembler ownerAssembler;
	@Autowired DogResrouceAssembler dogAssembler;
	@Autowired
	private DogService service;
	
	
	
	@GetMapping("/owners")
	CollectionModel<EntityModel<Owner>> all(){
		List<EntityModel<Owner>> owners = service.findAllOwners().stream().map(ownerAssembler::toModel).collect(Collectors.toList());
		 return new CollectionModel<>(owners, linkTo(methodOn(OwnerController.class).all()).withSelfRel());
	}

	@PostMapping("/owners")
	@ResponseStatus(HttpStatus.CREATED)
	EntityModel<?> newOwner(@Valid @RequestBody Owner newOwner) throws URISyntaxException{
		Owner addedOwner = service.saveEntity(newOwner);
		
		return ownerAssembler.toModel(addedOwner);	
	}
	@GetMapping("/owners/{id}")
	EntityModel<Owner> one(@PathVariable int id) {
		Owner owner= service.findOwner(id);
		return ownerAssembler.toModel(owner);
	}
	
	@PutMapping("/owners/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	EntityModel<?> replaceOwner(@Valid @RequestBody Owner newOwner, @PathVariable int id) {
		
		Owner updatedOwner = service.updateOwner(id, newOwner);
		return ownerAssembler.toModel(updatedOwner);	
	}
	
	@DeleteMapping("/owners/{id}")
	ResponseEntity<?> deleteOwner(@PathVariable int id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/owners/{id}/dogs")
	CollectionModel<EntityModel<Dog>> allDogs(@PathVariable int id){
		List<EntityModel<Dog>> dogs = service.findAllDogsByOwner(id).stream()
				.map(dogAssembler::toModel)
				.collect(Collectors.toList());
				 
		 return new CollectionModel<>(dogs);
	}

	
}

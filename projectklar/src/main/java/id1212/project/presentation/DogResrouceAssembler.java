package id1212.project.presentation;
import org.springframework.stereotype.Component;

import id1212.project.domain.Dog;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
@Component
public class DogResrouceAssembler implements RepresentationModelAssembler<Dog, EntityModel<Dog>>{
		@Override
		public EntityModel<Dog> toModel(Dog dog) {
			return new EntityModel<>(dog,
					linkTo(methodOn(DogController.class).oneDog(dog.getId())).withSelfRel(),
					linkTo(methodOn(DogController.class).allDogs()).withRel("dogs"),
					linkTo(methodOn(OwnerController.class).one(dog.getOwner().getOwnerId())).withRel("owner"));
		}

}


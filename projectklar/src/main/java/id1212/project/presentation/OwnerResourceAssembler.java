package id1212.project.presentation;
import org.springframework.stereotype.Component;


import id1212.project.domain.Owner;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
@Component
public class OwnerResourceAssembler implements RepresentationModelAssembler<Owner, EntityModel<Owner>>{

	@Override
	public EntityModel<Owner> toModel(Owner owner) {
		
		return new EntityModel<>(owner,
				linkTo(methodOn(OwnerController.class).one(owner.getOwnerId())).withSelfRel(),
				linkTo(methodOn(OwnerController.class).allOwners()).withRel("owner"),
				linkTo(methodOn(OwnerController.class).allDogs(owner.getOwnerId())).withSelfRel());
				
				
	}

}

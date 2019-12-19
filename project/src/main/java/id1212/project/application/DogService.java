package id1212.project.application;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import id1212.project.domain.Dog;
import id1212.project.domain.Owner;
import id1212.project.domain.User;
import id1212.project.presentation.DogResrouceAssembler;
import id1212.project.presentation.OwnerResourceAssembler;
import id1212.project.presentation.error.ErrorException;
import id1212.project.repository.DogRepository;
import id1212.project.repository.OwnerRepository;
import id1212.project.repository.UserRepository;


@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
@Service
public class DogService {
@Autowired
OwnerRepository ownerRep;
@Autowired
DogRepository dogRep;
@Autowired
private  OwnerResourceAssembler ownerAssembler;
@Autowired
private DogResrouceAssembler dogAssembler;
@Autowired
PasswordEncoder passwordEncoder;
@Autowired
UserRepository userRep;

	public List<EntityModel<Owner>> findAllOwners() {
		List<EntityModel<Owner>> owners = ownerRep.findAll().stream().map(ownerAssembler::toModel).collect(Collectors.toList());
	
		return owners;
	}

	public EntityModel<Owner> saveEntity(Owner newOwner) {
		Owner owner = ownerRep.save(newOwner);
			if(owner.equals(null)) {
				throw new ErrorException("Owner could not be created with id: ", newOwner.getOwnerId());
			}
		return ownerAssembler.toModel(owner);
	}

	public Owner findOwner(int id) {
		Optional<Owner> owner = ownerRep.findById(id);
		if(owner.isEmpty()) {
			throw new ErrorException("Owner was not found with id: ", id);
		}
		return  owner.get();
	}

	public EntityModel<Owner> updateOwner(int id, Owner newOwner) {
		Owner updatedOwner= ownerRep.findById(id).map(owner -> {
		
			owner=setOwnerParam(owner, newOwner);

			return ownerRep.save(owner);
		}).orElseGet(()->{
			throw new ErrorException("Owner not found with id: ", id);
		});
		if(updatedOwner.equals(null)) {
			throw new ErrorException("Owner could not be updated with id: ", id);
		}
		
		return  ownerAssembler.toModel(updatedOwner);
	}
	
	public void delete(int id) {
		if(ownerRep.existsById(id)) {
			ownerRep.deleteById(id);
		}
		else
			throw new ErrorException("Entity Could not be found, and has not been deleted", id);
	
		
	}



/**
 *  DOG REQUESTS START HERE. MAYBE SPLIT UP into two services..
 */
	public List<EntityModel<Dog>> findAllDogs(int id) {
		List<EntityModel<Dog>> dogs = dogRep.findDogByOwner_OwnerId(id).stream()
		.map(dogAssembler::toModel)
		.collect(Collectors.toList());
		return dogs;
	}

	public List<EntityModel<Dog>> findAllDogs() {
		List<EntityModel<Dog>> dogs = dogRep.findAll().stream().map(dogAssembler::toModel).collect(Collectors.toList());
		
		return dogs;
	}

	public Dog findDog(int id) {
		System.out.println("findDog");
		Optional<Dog> dog = dogRep.findById(id);
		if(dog.isEmpty()) {
			throw new ErrorException("Dog was not found with id: ", id);
		}
		return  dog.get();
	}

	public EntityModel<Dog> saveDog(Dog newDog) {
		int ownerId = newDog.getOwner().getOwnerId();
		Optional<Owner> owner = ownerRep.findById(ownerId);
		if(owner.isPresent()) {
			Dog dog = dogRep.save(newDog);
			owner.get().addDog(dog);
			if(dog.equals(null)) {
				throw new ErrorException("Dog could not be created with id: ", newDog.getId());
			}
		return dogAssembler.toModel(dog);
		}
		else {
			throw new ErrorException("Dog could not be created for owner with id: ", ownerId);
		}
	}

	public EntityModel<Dog> updateDog(int id, Dog newDog) {
		Dog updatedDog= dogRep.findById(id).map(dog -> {
			dog=setDogParam(dog, newDog);
			return dogRep.save(dog);
		}).orElseGet(()->{
			newDog.setId(id);
			return dogRep.save(newDog);
		});
		if(updatedDog.equals(null)) {
			throw new ErrorException("Dog could not be updated with id: ", newDog.getId());
		}
		
		return  dogAssembler.toModel(updatedDog);
	}

	public void deleteDog(int id) {
		if(dogRep.existsById(id)) {
			Dog dog = dogRep.findById(id).get();
			Owner owner = dog.getOwner();
			owner.removeDog(dog);
			dogRep.deleteById(id);
			System.out.println("delete: "+id);
			}
			else
				throw new ErrorException("Dog was not found and could not be deleted with id ", id);
	}
	
	
	private Owner setOwnerParam(Owner owner, Owner newOwner) {
		owner.setSurName(newOwner.getSurName());
		owner.setLastName(newOwner.getLastName());
		
		return owner;
	}
	private Dog setDogParam(Dog dog, Dog newDog) {
		dog.setAge(newDog.getAge());
		dog.setName(newDog.getName());
		dog.setOwner(newDog.getOwner());
		dog.setRace(newDog.getRace());
		return dog;
	}

	public void saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRep.save(user);
		System.out.println(user.getUsername() + user.getRole()+ user.getPassword());
		
	}
}

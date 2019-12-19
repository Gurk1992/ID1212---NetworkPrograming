package id1212.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import id1212.project.domain.Dog;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface DogRepository extends JpaRepository<Dog, Integer> {

	List<Dog> findDogByOwner_OwnerId(int ownerId);
}

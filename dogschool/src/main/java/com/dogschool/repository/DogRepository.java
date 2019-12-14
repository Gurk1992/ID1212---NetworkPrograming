package com.dogschool.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dogschool.domain.Dog;



@RepositoryRestResource(path="dogs", collectionResourceRel="dogs" )
@Transactional(propagation = Propagation.MANDATORY)
public interface DogRepository extends PagingAndSortingRepository<Dog, Integer> {

}

package com.dogschool.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dogschool.domain.Owner;


@RepositoryRestResource(path="owners", collectionResourceRel="owners" )
@Transactional(propagation = Propagation.MANDATORY)
public interface OwnerRepository extends PagingAndSortingRepository<Owner, Integer> {

}

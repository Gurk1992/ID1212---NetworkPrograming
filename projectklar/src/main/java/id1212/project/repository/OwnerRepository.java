package id1212.project.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import id1212.project.domain.Owner;
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

}

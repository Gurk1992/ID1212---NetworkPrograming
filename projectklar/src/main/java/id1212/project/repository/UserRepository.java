package id1212.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import id1212.project.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	User findByUsername(String username);
}

package id1212.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import id1212.project.domain.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	@Autowired
	
	User findByUsername(String username);
}

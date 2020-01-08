package id1212.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import id1212.project.domain.User;
import id1212.project.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{

@Autowired
private UserRepository userRep;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 User user = userRep.findByUsername(username);
	        if(user == null){
	            throw new UsernameNotFoundException("UserName "+username+" not found");
	        }
	        return new UserDetail(user);
	    }	
}

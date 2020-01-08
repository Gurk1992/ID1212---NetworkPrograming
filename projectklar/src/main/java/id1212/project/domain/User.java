package id1212.project.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class User extends AuthorizationServerConfigurerAdapter  {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  int userId;
	
	@NotNull
	private String username;
	@NotNull
	private String password;
	/*@NotNull
	private String role;*/
	public User(String username, String password/*, String role*/) { 
		
		this.username = username; 
		this.password = password; 
		//this.role=role;
	
	} 
	

}

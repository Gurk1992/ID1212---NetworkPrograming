package id1212.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@EnableTransactionManagement // Needed for @Transactional attribute outside repositories.
@EnableWebMvc
@Configuration
public class DogConfig  {

}

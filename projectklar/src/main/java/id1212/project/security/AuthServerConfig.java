package id1212.project.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	  @Qualifier("authenticationManagerBean")
	  private AuthenticationManager authenticationManager;
	  @Autowired
	  private UserService userService;
	  @Autowired
	  private PasswordEncoder passwordEncoder;
	  @Override
	  public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		  oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	  }
	  @Override
	  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
	    clients.inMemory()
	      .withClient("foo").secret(passwordEncoder.encode("123"))
	      .authorizedGrantTypes("password",  "refresh_token").scopes("GET", "POST", "PUT", "Delete")
	      .autoApprove(true);
	  }
	  @Override
	  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	    endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager).accessTokenConverter(defaultAccessTokenConverter())
	      .userDetailsService(userService);
	  }
	  @Bean
	  public TokenStore tokenStore(){
	    return new JwtTokenStore(defaultAccessTokenConverter());
	  }
	  @Bean
	  public JwtAccessTokenConverter defaultAccessTokenConverter() {
	    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
	    converter.setSigningKey("123");
	    return converter;
	  }
}
	


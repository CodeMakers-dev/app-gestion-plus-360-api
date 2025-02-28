package com.gestion.plus.api.config;

import org.springframework.context.annotation.Bean;





import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity 
public class WebSecurityConfig {

   @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) 
    throws Exception { 
    	return authenticationConfiguration.getAuthenticationManager();
    }
   @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
  @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
           .csrf(csrf -> csrf.disable())
           .authorizeHttpRequests((authz) -> authz
               .requestMatchers("/api/v1/Usuario/**").permitAll() 
               .requestMatchers("/api/**").permitAll() 
               .anyRequest().authenticated()
           );
       return http.build();
   }

}

package com.haven.securities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	public SecurityConfig() {
        logger.info("SecurityConfig initialized");
    }
	
	  @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
	        return http
	            .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF cho API/WebSocket
	            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                // Public endpoints
	                .requestMatchers(
	                    "/api/auth/**",
	                    "/chat/info",
	                    "/customer_chat.html", 
	                    "/shop_chat.html"
	                ).permitAll()
	                
	                // WebSocket endpoints - cần public cho kết nối ban đầu
	                .requestMatchers(
	                    "/chat/**",
	                    "/topic/**",
	                    "/app/**"
	                ).permitAll() // Hoặc hasAnyRole("CUSTOMER", "SHOP") nếu muốn bảo mật
	                
	                .requestMatchers(
	                        "/swagger-ui/**",
	                        "/v3/api-docs/**",
	                        "/swagger-ui.html",
	                        "/swagger-resources/**",
	                        "/webjars/**"
	                    ).permitAll()
	                
	                // Các endpoint REST
	                .requestMatchers("/api/admin/**").hasRole("ADMIN")
	                .requestMatchers("/api/shop/**").hasRole("SHOP")    
	                .requestMatchers("/api/chat/**").hasAnyRole("CUSTOMER", "SHOP")
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	    }
	

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(authProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }
}

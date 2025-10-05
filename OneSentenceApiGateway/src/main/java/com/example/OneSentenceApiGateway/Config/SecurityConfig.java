package com.example.OneSentenceApiGateway.Config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.OneSentenceApiGateway.Util.JwtUtil;
import com.example.OneSentenceApiGateway.filters.JwtAuthenticationFilter;
import com.example.OneSentenceApiGateway.filters.JwtAuthenticationManager;
import com.example.OneSentenceApiGateway.filters.JwtServerAuthenticationConverter;
import com.example.OneSentenceApiGateway.filters.JwtValidatorFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	private  final String freeUrl[] = {"/user/login", "/user/register/**", "/user/login/**" , "/actuator/**" , "/swagger-ui.html/**" , "/swagger-ui/**"
			, "/swagger-resources/", "/api-docs/**" , "/aggregate/**" , "/user/validate" , "/generate-token"};
	
	@Autowired
	private JwtUtil jwtutil;
	
    private final JwtAuthenticationManager authenticationManager;
    private final WebClient.Builder webClient;
    private final RestClient.Builder restClient;
    private final JwtServerAuthenticationConverter authenticationConverter;


	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public SecurityConfig(JwtAuthenticationManager authenticationManager, WebClient.Builder webClient , JwtServerAuthenticationConverter authenticationConverter) {
        this.authenticationManager = authenticationManager;
        this.webClient = webClient;
		this.restClient = null;
		this.authenticationConverter = authenticationConverter;
    }

    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JwtAuthenticationManager authenticationManager,
                                                         JwtServerAuthenticationConverter jwtConverter) {

        JwtAuthenticationFilter authenticationWebFilter = new JwtAuthenticationFilter(jwtutil, webClient, restClient);
        JwtValidatorFilter validatorFilter = new JwtValidatorFilter(authenticationManager);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(freeUrl).permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(validatorFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authenticationManager)
                .build();
    }
}

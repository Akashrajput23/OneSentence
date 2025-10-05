package com.example.OneSentenceApiGateway.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.OneSentenceApiGateway.DTO.JwtAuthenticationToken;

import reactor.core.publisher.Mono;

@Component
public class JwtValidatorFilter implements WebFilter {
	
	public final JwtAuthenticationManager authenticationManager;

	public JwtValidatorFilter(JwtAuthenticationManager authenticationManager2) {
		this.authenticationManager = authenticationManager2;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("Token--->" +authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            JwtAuthenticationToken jwtToken = new JwtAuthenticationToken(token);
            Mono<Authentication> authResult =  authenticationManager.authenticate(jwtToken);
            
            authResult
            .flatMap(auth -> {
            	
            	if(auth.isAuthenticated()) {
                	System.out.println("inside if vali");
    				SecurityContextHolder.getContext().setAuthentication(auth);
    				return chain.filter(exchange);
            	}else {
                	System.out.println("inside else vali");
            		return chain.filter(exchange);
	
            	}
            });   
        }
		return chain.filter(exchange);
	}

}

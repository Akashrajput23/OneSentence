package com.example.OneSentenceApiGateway.filters;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Function;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.example.OneSentenceApiGateway.DTO.JwtAuthenticationToken;
import com.example.OneSentenceApiGateway.DTO.LoginRequest;
import com.example.OneSentenceApiGateway.Util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager<R>  implements ReactiveAuthenticationManager{
	
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClient;

    
    public JwtAuthenticationManager(JwtUtil jwtUtil, WebClient.Builder builder) {
        this.jwtUtil = jwtUtil;
        this.webClient = builder;
    }


	@Override
	public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
//        String token = authentication.getCredentials().toString();
		String token = ((JwtAuthenticationToken) authentication).getToken();
        String username = jwtUtil.validateAndExtractUsername(token);
        
        System.out.println("Username----> "+ username);
        LoginRequest loginRequest = new LoginRequest(null , null);
        loginRequest.setUsername(username);
        return  webClient.build()
        .post()
        .uri("http://localhost:8083/user/validate")
        .bodyValue(loginRequest)
        .retrieve()
        .bodyToMono(Boolean.class)
         .flatMap(ifExist -> {
            if (Boolean.TRUE.equals(ifExist)) {
            	System.out.println("inside if");
                return Mono.just(new UsernamePasswordAuthenticationToken(
                        username,
                        token,
                        Collections.emptyList() // you can fetch roles/authorities here
                ));
            } else {
            	System.out.println("inside else");

                return Mono.error(new BadCredentialsException("User not valid"));
            }
        });
	}
}

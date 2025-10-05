package com.example.OneSentenceApiGateway.filters;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.example.OneSentenceApiGateway.DTO.LoginRequest;
import com.example.OneSentenceApiGateway.Util.JwtUtil;

import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter implements WebFilter  {
	
	private final JwtUtil jwtUtil;
    private final WebClient.Builder webClient;
    private final RestClient.Builder restClient;

	
	public JwtAuthenticationFilter( JwtUtil jwtUtil ,WebClient.Builder webClient, RestClient.Builder restClient ) {
		this.jwtUtil = jwtUtil;
		this.webClient = webClient;
		this.restClient = restClient;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
	    if (!exchange.getRequest().getPath().toString().equals("/generate-token")) {
	        return chain.filter(exchange);
	    }

	    org.springframework.http.HttpHeaders header = exchange.getRequest().getHeaders();
	    String username = header.getFirst("username");
	    String password = header.getFirst("password");

	    LoginRequest loginRequest = new LoginRequest(username, password);
	   
	    return webClient.build().post()
	            .uri("http://localhost:8083/user/login")
	            .bodyValue(loginRequest)
	            .retrieve()
	            .bodyToMono(Boolean.class)
	            .flatMap(ifExist -> {
	                if (Boolean.TRUE.equals(ifExist)) {
	                    // ✅ User exists -> generate token
	                    String token = jwtUtil.generateToken(username, 500);
	                    String responseBody = String.format("{\"token\": \"%s\"}", token);

	                    DataBuffer buffer = exchange.getResponse()
	                            .bufferFactory()
	                            .wrap(responseBody.getBytes(StandardCharsets.UTF_8));

	                    return exchange.getResponse().writeWith(Mono.just(buffer));
	                } else {
	                    // ❌ User not found -> send 401
	                    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
	                    DataBuffer buffer = exchange.getResponse()
	                            .bufferFactory()
	                            .wrap("{\"error\": \"Invalid credentials\"}".getBytes(StandardCharsets.UTF_8));
	                    return exchange.getResponse().writeWith(Mono.just(buffer));
	                }
	            });
	}

	


}

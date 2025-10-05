package com.example.OneSentenceApiGateway.Util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.boot.autoconfigure.info.ProjectInfoProperties.Build;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private static final String SCERET_KEY = "your-secure-secret-key-min-32bytes";
//	private static final java.security.Key Key = Keys.hmacShaKeyFor(SCERET_KEY.getBytes(StandardCharsets.UTF_8));
	
	public String generateToken(String username , long expiryMinutes) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000))
				.signWith(Keys.hmacShaKeyFor(SCERET_KEY.getBytes(StandardCharsets.UTF_8)),SignatureAlgorithm.HS256)
				.compact();
	}
	
	public String validateAndExtractUsername(String token) {
		return Jwts.parserBuilder()
				.setAllowedClockSkewSeconds(1)
				.setSigningKey(Keys.hmacShaKeyFor(SCERET_KEY.getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}



}

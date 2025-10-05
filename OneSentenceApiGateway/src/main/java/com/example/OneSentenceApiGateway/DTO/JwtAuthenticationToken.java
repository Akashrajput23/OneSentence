package com.example.OneSentenceApiGateway.DTO;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.w3c.dom.views.AbstractView;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
	
	public String token;
	
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public JwtAuthenticationToken(String token) {
		super(null);
		this.token = token;
		setAuthenticated(false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

}

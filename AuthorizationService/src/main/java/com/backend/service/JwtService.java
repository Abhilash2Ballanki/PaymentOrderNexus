package com.backend.service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.entity.Role;
import com.backend.entity.UserCredential;
import com.backend.repository.UserCredentialRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
	
	@Autowired
	private UserCredentialRepository credentialRepository;
	
	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
	
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		UserCredential user = credentialRepository.findByUsername(username).get();
		List<String> listOfRoles = new ArrayList<>();
		for(Role role:user.getRoles()) {
			listOfRoles.add(role.getRole_name());
		}
		claims.put("roles", listOfRoles);
		return createToken(claims,username);
	}

	private String createToken(Map<String, Object> claims, String username) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+(1000*60*60)))
				.signWith(getSignKey(),SignatureAlgorithm.HS256)
				.compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public void validateToken(final String token) {
		Jwts.parserBuilder().setSigningKey(getSignKey())
		.build().parseClaimsJws(token);
	}
	

}

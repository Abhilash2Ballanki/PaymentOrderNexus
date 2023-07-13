package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.entity.UserCredential;
import com.backend.repository.UserCredentialRepository;


@Service
public class AuthService {
	
	@Autowired
	private UserCredentialRepository repository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private JwtService jwtService;

	public UserCredential saveUser(UserCredential credential) {
		credential.setPassword(encoder.encode(credential.getPassword()));
		return repository.save(credential);
	}
	
	public String generateToken(String username) {
		return jwtService.generateToken(username);
	}
	
	public void validateToken(String token) {
		jwtService.validateToken(token);
	}
	
}

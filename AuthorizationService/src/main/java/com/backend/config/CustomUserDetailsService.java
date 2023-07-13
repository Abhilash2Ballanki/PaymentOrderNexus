package com.backend.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.backend.entity.UserCredential;
import com.backend.repository.UserCredentialRepository;


public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserCredentialRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserCredential> credential = repository.findByUsername(username);
		return credential.map(CustomDetails::new).orElseThrow(() -> 
		new RuntimeException("user not found"));
	}

}

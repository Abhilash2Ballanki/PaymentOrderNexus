package com.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.AuthRequest;
import com.backend.entity.UserCredential;
import com.backend.service.AuthService;



@RestController
@RequestMapping("/auth")
public class SecurityController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/newUser")
	public UserCredential addUser(@RequestBody UserCredential credential) {
		return authService.saveUser(credential);
	}
	
	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthRequest authRequest) {
		Authentication authenticate = authenticationManager
		.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if(authenticate.isAuthenticated()) {
			return authService.generateToken(authRequest.getUsername());
		}
		else {
			throw new RuntimeException("Invalid access");
		}
	}
	
	@GetMapping("/validate")
	public String validateToken(@RequestParam String token) {
		authService.validateToken(token);
		return "Token is valid";
	}

}

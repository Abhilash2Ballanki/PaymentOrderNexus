package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.entity.UserCredential;


public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer>{
	
	Optional<UserCredential> findByUsername(String username);

}

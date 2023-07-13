package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Entiry.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
   Optional<Account> findByAccountId(String accountId);
}


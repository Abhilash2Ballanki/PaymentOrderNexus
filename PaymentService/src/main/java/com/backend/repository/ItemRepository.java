package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Entiry.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {
	Item findByname(String name);
}

package com.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Entity.Orders;

public interface OrderRepository extends JpaRepository<Orders, Integer> {

	Orders findByItem(String item);
}

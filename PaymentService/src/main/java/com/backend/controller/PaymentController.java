package com.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.Entiry.Orders;
import com.backend.Exception.InsufficientAmountException;
import com.backend.Exception.ItemNotFoundException;
import com.backend.Exception.PaymentFailedException;
import com.backend.service.PaymentService;

@RestController
@RequestMapping("/pay")
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	public PaymentService paymentService;

	@PostMapping("/payments")
	public ResponseEntity<String> makePayment(@RequestBody Orders orderRequest) {
		try {
			paymentService.makePayment(orderRequest);

			logger.info("Payment successful for user: {} with CorelationId: {}",orderRequest.getEmail(), orderRequest.getCorelation_id());

			return ResponseEntity.ok("Payment successful.");

		} catch (InsufficientAmountException e) {

			logger.error("Insufficient amount or give proper bank name for user: {} with CorelationId: {}", orderRequest.getEmail(),
					orderRequest.getCorelation_id());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient amount.");
		} catch (ItemNotFoundException e) {

			logger.error("Item not found of user: {} with CorelationId: {}",orderRequest.getEmail(), orderRequest.getCorelation_id());

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
		} catch (PaymentFailedException e) {

			logger.error("Payment failed for the user: {} with CorelationId: {}", orderRequest.getEmail(),orderRequest.getCorelation_id());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed.");
		}
	}
}

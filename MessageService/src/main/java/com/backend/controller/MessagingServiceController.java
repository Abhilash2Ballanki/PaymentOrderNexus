package com.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.service.MessagingService;

@RestController
@RequestMapping("/message")
public class MessagingServiceController {

	private static final Logger logger = LoggerFactory.getLogger(MessagingServiceController.class);

	@Autowired
	public MessagingService messagingService;

	@PostMapping("/process-payment")
	public ResponseEntity<String> processPayment(@RequestBody String message) {
		String[] emailAndCorrelationID = messagingService.extractEmailAndCorrelationID(message);
		String email = null;
		String correlationID = null;

		if (emailAndCorrelationID != null) {
			email = emailAndCorrelationID[0];
			correlationID = emailAndCorrelationID[1];
			String paymentMessage = messagingService.extractPaymentMessage(message);
			if (paymentMessage != null) {
				logger.info("Payment processed successfully for user: {} with Correlation ID: {}", email,
						correlationID);
			} else {
				logger.error("Invalid payment message format: {}. Payment info not found.", message);
			}
		} else {
			logger.error("Invalid payment message format: {}. user and CorrelationId not found.", message);
		}

		messagingService.receivePaymentMessage(message);
		if (email != null && correlationID != null) {
			logger.info("Payment message processed successfully for user: {} with Correlation ID: {}", email,
					correlationID);
		}
		return ResponseEntity.ok("Payment message received and processed successfully for user: {} with Correlation ID: {}");
	}
}

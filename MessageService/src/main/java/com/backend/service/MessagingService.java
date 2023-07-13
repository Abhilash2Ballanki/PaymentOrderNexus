package com.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

	@Autowired
	public EmailService emailService;

	@RabbitListener(queues = "paymentQueue")
	public void receivePaymentMessage(String message) {
		String[] emailAndCorrelationID = extractEmailAndCorrelationID(message);
		if (emailAndCorrelationID != null) {
			String email = emailAndCorrelationID[0];
			String correlationId = emailAndCorrelationID[1];
			String paymentMessage = extractPaymentMessage(message);
			if (paymentMessage != null) {
				String subject = "Payment Details";
				String body = paymentMessage + " with CorelationId: " + correlationId;

				try {
					emailService.sendEmail(email, subject, body, correlationId);
					logger.info("Payment notification sent successfully for user: {} with CorelationId: {}", email,
							correlationId);
				} catch (Exception e) {
					logger.error(
							"Error sending payment notification for user: {} with CorelationId: {}. Error message: {}",
							email, correlationId, e.getMessage());
				}
			} else {
				logger.error("Invalid payment message format: {}. Payment message not found.", message);
			}
		} else {
			logger.error("Invalid payment message format: {}. Email and correlationId not found.", message);
		}
	}

	public String[] extractEmailAndCorrelationID(String message) {
		String[] paymentDetails = message.split(" Email: ");
		if (paymentDetails.length == 2) {
			String[] emailAndCorrelationID = paymentDetails[1].split(" CorelationId: ");
			if (emailAndCorrelationID.length == 2) {
				return emailAndCorrelationID;
			}
		}
		return null;
	}

	public String extractPaymentMessage(String message) {
		String[] paymentDetails = message.split(" Email: ");
		if (paymentDetails.length == 2) {
			return paymentDetails[0];
		}
		return null;
	}
}

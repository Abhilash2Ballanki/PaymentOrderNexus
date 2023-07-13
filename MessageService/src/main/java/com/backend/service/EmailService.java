package com.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	public JavaMailSender mailSender;

	public void sendEmail(String recieverEmail, String subject, String body, String Corelation_id) {
		logger.info("Sending email to: {}, Subject: {} for Corelation-ID: {}", recieverEmail, subject, Corelation_id);

		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(recieverEmail);
		message.setSubject(subject);
		message.setText(body);

		mailSender.send(message);
		logger.info("Email sent successfully to the user: {} for Corelation-ID: {}", recieverEmail, Corelation_id);

	}
}

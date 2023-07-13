package com.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.Entiry.Account;
import com.backend.Entiry.Item;
import com.backend.Entiry.Orders;
import com.backend.Exception.InsufficientAmountException;
import com.backend.Exception.ItemNotFoundException;
import com.backend.Exception.PaymentFailedException;
import com.backend.repository.AccountRepository;
import com.backend.repository.ItemRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Autowired
	public RabbitTemplate rabbitTemplate;

	@Autowired
	public AccountRepository accountRepository;

	@Autowired
	public ItemRepository itemRepository;

	@Override
	public void makePayment(Orders orderRequest) {
		Item item = itemRepository.findByname(orderRequest.getItem());

		if (item == null) {
			logger.error("Item not found:{} of user: {} with CorelationId: {}", orderRequest.getItem(), orderRequest.getEmail(),
					orderRequest.getCorelation_id());
			throw new ItemNotFoundException("Item not found: " + orderRequest.getItem());
		}

		double totalAmount = item.getAmount() * orderRequest.getQuantity();
		Account account = accountRepository.findByAccountId(orderRequest.getBankname())
				.orElseThrow(() -> new PaymentFailedException("Your bank is not valid"));

		if (account == null || account.getAmount() < totalAmount) {
			logger.error("Insufficient amount in the account of user: {} with CorelationId: {}", orderRequest.getEmail(),
					orderRequest.getCorelation_id());
			throw new InsufficientAmountException("Insufficient amount in the account.");
		}

		Integer newAmount = (int) (account.getAmount() - totalAmount);
		account.setAmount(newAmount);
		accountRepository.save(account);

		logger.info("Payment successful for item:{}. Deducting amount from the account of user: {} with CorelationId: {}", orderRequest.getItem(),orderRequest.getEmail(),
				orderRequest.getCorelation_id());

		String bank = orderRequest.getBankname().toUpperCase();

		String paymentMessage = "Payment successful for : " + orderRequest.getQuantity() + " " + orderRequest.getItem()
				+ " with amount Rs: " + totalAmount + " from Bank " + bank;
		String paymentDetails = paymentMessage + " Email: " + orderRequest.getEmail() + " CorelationId: "
				+ orderRequest.getCorelation_id();
		

		logger.info("Sending message through RabbitMQ to MessageService, msg: {} for user: {} with CorelationId: {}",
				paymentDetails, orderRequest.getEmail(), orderRequest.getCorelation_id());
		rabbitTemplate.convertAndSend("paymentQueue", paymentDetails);
		logger.info("Payment processing completed for user: {} with CorelationId: {}", orderRequest.getEmail(),
				orderRequest.getCorelation_id());

	}

}

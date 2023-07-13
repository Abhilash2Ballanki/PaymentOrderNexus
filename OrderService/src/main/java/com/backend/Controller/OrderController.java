package com.backend.Controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.Entity.Orders;
import com.backend.Exception.InsufficientQuantityException;
import com.backend.Exception.ItemNotFoundException;
import com.backend.Service.OrderService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@RequestMapping("/api")
@RateLimiter(name = "OrderServiceLimiter")
public class OrderController {

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	public OrderService orderService;

	@RateLimiter(name = "orderApiLimiter", fallbackMethod = "orderNotPlacedDueToMoreLoad")
	@PostMapping("/orders")
	public ResponseEntity<String> placeOrder(HttpServletRequest request, @RequestBody Orders orderRequest) {
		String coRelationId = request.getHeader("Co-relationID");
		String email = request.getHeader("EMAIL");
		String bankName = request.getHeader("BANK");
		String jwtToken = request.getHeader("Authorization");
		logger.info("Placing order for user: {} with CorelationId: {}", email, coRelationId);

		orderRequest.setBankname(bankName);
		orderRequest.setEmail(email);
		orderRequest.setCorelation_id(coRelationId);
		orderRequest.setJwtToken(jwtToken);
		try {
			orderService.placeOrder(orderRequest);
			logger.info("Order placed successfully for item:{} of user:{} with CorelationId: {}",
					orderRequest.getItem(), email, coRelationId);

			return ResponseEntity.ok("Order and payment done successfully");
		} catch (ItemNotFoundException e) {
			logger.error("Exception while ordering item:{} of user: {} with CorelationId: {}, exception: {}",
					orderRequest.getItem(), email, coRelationId, e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (InsufficientQuantityException e) {
			logger.error("Insufficient quantity of item:{} of user: {} with CorelationId: {}, exception: {}",
					orderRequest.getItem(), email, coRelationId, e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<String> orderNotPlacedDueToMoreLoad(HttpServletRequest request, Orders orderRequest,
			Exception exception) {
		return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)

				.body("Server is not accepting any requests as request limit is exceeded");

	}
}

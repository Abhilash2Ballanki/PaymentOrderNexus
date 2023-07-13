package com.backend.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import com.backend.jwtUtil.JwtUtil;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Autowired
	private RouteValidator routeValidator;

	@Autowired
	private JwtUtil jwtUtil;

	public AuthorizationFilter() {
		super(Config.class);
	}

	public static class Config {
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpRequest modifiedReq = null;
			if (routeValidator.isSecured.test(exchange.getRequest())) {
				String REQUEST_URI = exchange.getRequest().getURI().toString();
				logger.info("Authorization Filter check is started before routing to the {} request",
						REQUEST_URI);
				if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
					logger.error("This {} request is not containing the Authorization Header", REQUEST_URI);
					throw new RuntimeException("Authorization is missing");
				}
				String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
				if (authHeader != null && authHeader.startsWith("Bearer ")) {
					authHeader = authHeader.substring(7);
				}
				try {
					jwtUtil.validateToken(authHeader);
					logger.info("Authorization token is validated successfully");
					ServerHttpRequest request = exchange.getRequest();
					String co_relationId = request.getHeaders().getFirst("Co-relationID");
					if(co_relationId == null) {
						modifiedReq = request.mutate().header("Co-relationID", UUID.randomUUID().toString()).build();
						co_relationId = modifiedReq.getHeaders().getFirst("Co-relationID");
						exchange = exchange.mutate().request(modifiedReq).build();
					}
					logger.info("BEGIN Routing the request to the {} with Co-relationId {}", REQUEST_URI, co_relationId);
					return chain.filter(exchange);
				} catch (Exception exception) {
					logger.error("You are not an Authorized User and we cannot route to {}", REQUEST_URI);
					throw new RuntimeException(exception.getMessage());
				}

			}
			return chain.filter(exchange);
		});
	}

}

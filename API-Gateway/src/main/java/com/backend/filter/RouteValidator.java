package com.backend.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {

	public static final List<String> openApis = List.of("/auth/newUser", "/auth/authenticate", "/auth/validate","/actuator");

	Predicate<ServerHttpRequest> isSecured = request -> openApis.stream()
			.noneMatch(uri -> request.getURI().getPath().contains(uri));

}

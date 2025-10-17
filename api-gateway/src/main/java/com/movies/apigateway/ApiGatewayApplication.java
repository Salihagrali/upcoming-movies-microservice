package com.movies.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	// Can coexist with that default ones in the config file
	@Bean
	public RouteLocator globalRouteLocator(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
				.route("user-service",
						p -> p.path("/gateway/user-service/**")
								.filters(f -> f.rewritePath("/gateway/user-service/(?<segment>.*)", "/${segment}"))
								.uri("lb://USER-SERVICE"))
				.build();

	}
}

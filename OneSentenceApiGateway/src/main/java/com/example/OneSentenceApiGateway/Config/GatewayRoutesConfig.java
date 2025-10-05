package com.example.OneSentenceApiGateway.Config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
	    return builder.routes()
	        // User Service
	        .route("user-service", r -> r.path("/aggregate/user/**")
	                .filters(f -> f.rewritePath("/aggregate/user/(?<segment>.*)", "/${segment}"))
	                .uri("lb://USERSERVICE"))
	        .route("user-service-swagger", r -> r.path("/aggregate/user/v3/api-docs")
	                .filters(f -> f.rewritePath("/aggregate/user/v3/api-docs", "/v3/api-docs"))
	                .uri("lb://USERSERVICE"))

	        // Post Service
	        .route("post-service", r -> r.path("/aggregate/post/**")
	                .filters(f -> f.rewritePath("/aggregate/post/(?<segment>.*)", "/${segment}"))
	                .uri("lb://POSTSERVICE"))
	        .route("post-service-swagger", r -> r.path("/aggregate/post/v3/api-docs")
	                .filters(f -> f.rewritePath("/aggregate/post/v3/api-docs", "/v3/api-docs"))
	                .uri("lb://POSTSERVICE"))

	        // Comment Service
	        .route("comment-service", r -> r.path("/aggregate/comment/**")
	                .filters(f -> f.rewritePath("/aggregate/comment/(?<segment>.*)", "/${segment}"))
	                .uri("lb://COMMENT"))
	        .route("comment-service-swagger", r -> r.path("/aggregate/comment/v3/api-docs")
	                .filters(f -> f.rewritePath("/aggregate/comment/v3/api-docs", "/v3/api-docs"))
	                .uri("lb://COMMENT"))

	        .build();
	}
}

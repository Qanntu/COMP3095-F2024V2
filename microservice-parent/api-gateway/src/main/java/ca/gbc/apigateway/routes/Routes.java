package ca.gbc.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.*;

import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.routeId;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

import java.net.URI;

@Configuration
@Slf4j
public class Routes {

    @Value("${service.product-url}")
    private String productServiceUrl;

    @Value("${service.order-url}")
    private String orderServiceUrl;

    @Value("${service.inventory-url}")
    private String inventoryServiceUrl;


    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        log.info("Initializing product service route with URL with URL: {}", productServiceUrl);

        return route("product-service")
                .route(RequestPredicates.path("/api/product"), request -> {
                    log.info("Received request for product service: {}", request.uri());
                    return HandlerFunctions.http(productServiceUrl).handle(request);    
                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("productServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        log.info("Initializing order-service route with URL: {}", orderServiceUrl);

        return route("order-service")
                .route(RequestPredicates.path("/api/order"), request -> {
                    log.info("Received request for order-service: {}", request.uri());
                    return HandlerFunctions.http(orderServiceUrl).handle(request);
                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("orderServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        log.info("Initializing inventory-service route with URL: {}", inventoryServiceUrl);

        return route("inventory-service")
                .route(RequestPredicates.path("/api/inventory"), request -> {
                    log.info("Received request for inventory-service: {}", request.uri());
                    return HandlerFunctions.http(inventoryServiceUrl).handle(request);
                })
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("inventoryServiceCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();


    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute(){
        return route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                        HandlerFunctions.http(productServiceUrl))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("ProductSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute(){
        return route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                        HandlerFunctions.http(orderServiceUrl))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("OrderSwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();

    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute(){
        return route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                        HandlerFunctions.http(inventoryServiceUrl))
                .filter(setPath("/api-docs"))
                .filter(CircuitBreakerFilterFunctions
                        .circuitBreaker("InventorySwaggerCircuitBreaker", URI.create("forward:/fallbackRoute")))
                .build();
    }



    @Bean
    public RouterFunction<ServerResponse> fallbackRoute(){
        return route("fallbackRoute")
                .route(RequestPredicates.all(),
                        request->ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Service is Temporarily Unavailable, please try again later"))
                .build();

    }
}
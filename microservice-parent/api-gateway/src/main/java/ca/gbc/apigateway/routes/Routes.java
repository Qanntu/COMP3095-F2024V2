package ca.gbc.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.*;

import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@Slf4j
public class Routes {

    @Value("${service.product.url}")
    private String productServiceUrl;

    @Value("${service.order.url}")
    private String orderServiceUrl;


    @Bean
    public RouterFunction<ServerResponse> productServiceRoute(){
        log.info("Initializing product service route with URL with URL: {}", productServiceUrl);

        return GatewayRouterFunctions.route("product-service")
                .route(RequestPredicates.path("/api/product"), request -> {
                    log.info("Received request for product service: {}", request.uri());
                    try{
                        ServerResponse response = HandlerFunctions.http(productServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e){
                        log.error("Error ocurred while routing request: {}", e.getMessage());
                        return ServerResponse.status(500).body("An error ocurred routing");
                    }
                })
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute(){
        log.info("Initializing order service route with URL: {}", orderServiceUrl);

        return GatewayRouterFunctions.route("order-service")
                .route(RequestPredicates.path("/api/order"), request -> {
                    log.info("Received request for order service: {}", request.uri());

                    try{
                        ServerResponse response = HandlerFunctions.http(orderServiceUrl).handle(request);
                        return response;
                    } catch (Exception e) {
                        log.error("Error ocurred while routing request: {}", e.getMessage());
                        return ServerResponse.status(500).body("An error ocurred routing");
                    }
                })
                .build();
    }


}

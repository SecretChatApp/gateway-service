package pl.gozderapatryk.gatewayservice.routing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Routing {

    @Bean
    public RouterFunction<ServerResponse> routerFunction(RoutingHandlers routingHandlers) {
        return route(POST("/users").and(accept(MediaType.APPLICATION_JSON)), routingHandlers::createUser);
    }
}
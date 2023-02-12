package pl.gozderapatryk.gatewayservice.routing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.gozderapatryk.gatewayservice.dto.request.CreateUserDto;
import pl.gozderapatryk.gatewayservice.proxy.UsersServiceProxy;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RoutingHandlers {
    private final UsersServiceProxy usersServiceProxy;

    public Mono<ServerResponse> findByUsername(ServerRequest serverRequest) {
        var createUserDto = serverRequest.bodyToMono(CreateUserDto.class);
        return usersServiceProxy
                .createUser(createUserDto)
                .flatMap(userDto -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(userDto)));
    }
}

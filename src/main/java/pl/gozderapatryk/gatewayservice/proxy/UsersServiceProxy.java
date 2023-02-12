package pl.gozderapatryk.gatewayservice.proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import pl.gozderapatryk.gatewayservice.dto.UserDto;
import pl.gozderapatryk.gatewayservice.dto.request.CreateUserDto;
import reactor.core.publisher.Mono;

@Service
public class UsersServiceProxy {
    private final WebClient webClient;

    public UsersServiceProxy(@Qualifier("userServiceWebClient") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<UserDto> createUser(Mono<CreateUserDto> createUserDtoMono) {
        return webClient
                .post()
                .uri("/users")
                .body(BodyInserters.fromValue(createUserDtoMono))
                .retrieve()
                .bodyToMono(UserDto.class);
    }
}
package pl.gozderapatryk.gatewayservice.service;

import pl.gozderapatryk.gatewayservice.dto.UserDto;
import reactor.core.publisher.Mono;

public interface UserServiceProxy {
    Mono<UserDto> findByUsername(String username);
}

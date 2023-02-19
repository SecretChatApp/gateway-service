package pl.gozderapatryk.gatewayservice.service.impl;

import org.springframework.stereotype.Service;
import pl.gozderapatryk.gatewayservice.dto.UserDto;
import pl.gozderapatryk.gatewayservice.service.UserServiceProxy;
import reactor.core.publisher.Mono;

@Service
public class UserServicePoxyImpl implements UserServiceProxy {
    @Override
    public Mono<UserDto> findByUsername(String username) {
        return null;
    }
}

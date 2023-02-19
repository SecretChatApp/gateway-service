package pl.gozderapatryk.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import pl.gozderapatryk.gatewayservice.exception.AppAuthenticationException;
import pl.gozderapatryk.gatewayservice.service.UserServiceProxy;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService {

    private final UserServiceProxy userServiceProxy;

    public Mono<User> findByUsername(String username) {
        log.info("============================> UserDetailsService -> findByUsername");
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return userServiceProxy
                .findByUsername(username)
                .map(userFromDb -> new User(
                        userFromDb.getUsername(),
                        userFromDb.getPassword(),
                        true, true, true, true,
                        authorities))
                .switchIfEmpty(Mono.error(() -> new AppAuthenticationException("username not found")));
    }

}

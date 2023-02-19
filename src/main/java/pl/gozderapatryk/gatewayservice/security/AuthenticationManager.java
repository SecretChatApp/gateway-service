package pl.gozderapatryk.gatewayservice.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.gozderapatryk.gatewayservice.exception.AppAuthenticationException;
import pl.gozderapatryk.gatewayservice.service.UserServiceProxy;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenManager tokenManager;
    private final UserServiceProxy userServiceProxy;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("==========================> AuthenticationManager -> authenticate");
            String authToken = authentication.getCredentials().toString();

            if (tokenManager.validateToken(authToken)) {
                Claims claims = tokenManager.getAllClaimsFromToken(authToken);
                String username = claims.getSubject();
                var authorities = new ArrayList<SimpleGrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority("USER"));
                return userServiceProxy
                        .findByUsername(username)
                        .map(userFromDb -> new UsernamePasswordAuthenticationToken(
                                userFromDb.getUsername(),
                                null,
                                authorities
                        ));
            }

            return Mono.error(() -> new AppAuthenticationException("authentication 1 exception"));
    }
}

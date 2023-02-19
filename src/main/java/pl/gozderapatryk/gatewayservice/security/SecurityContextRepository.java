package pl.gozderapatryk.gatewayservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("not supported yet");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
        ServerHttpRequest request = serverWebExchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("============================> SecurityContextRepository -> load -> authHeader: " + authHeader);
        log.info("====================> uri: " + serverWebExchange.getRequest().getURI());

        if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authToken, authToken);
            return this.authenticationManager.authenticate(authentication).map(SecurityContextImpl::new);
        }

        return Mono.error(new IllegalStateException("authentication failed 3"));
    }
}
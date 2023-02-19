package pl.gozderapatryk.gatewayservice.routing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.gozderapatryk.gatewayservice.dto.AuthRequestDto;
import pl.gozderapatryk.gatewayservice.dto.RefreshTokenDto;
import pl.gozderapatryk.gatewayservice.dto.TokensDto;
import pl.gozderapatryk.gatewayservice.security.TokenManager;
import pl.gozderapatryk.gatewayservice.security.UserDetailsService;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutingHandlers {

    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("=======================> login");
        Mono<AuthRequestDto> authRequestDtoMono = serverRequest.bodyToMono(AuthRequestDto.class);
        return authRequestDtoMono
                .flatMap(authRequestDto -> userDetailsService
                        .findByUsername(authRequestDto.getUsername())
                        .filter(userDetails -> passwordEncoder.matches(authRequestDto.getPassword(), userDetails.getPassword()))
                        .flatMap(user -> tokenManager
                                .generateTokens(user)
                                .flatMap(tokens -> ServerResponse
                                        .ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(TokensDto
                                                .builder()
                                                .accessToken(tokens.getAccessToken())
                                                .refreshToken(tokens.getRefreshToken())
                                                .build()))
                                )))
                .onErrorResume(e -> ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(e.getMessage())));
    }

    public Mono<ServerResponse> refreshTokens(ServerRequest serverRequest) {
        Mono<RefreshTokenDto> refreshTokenDtoMono = serverRequest.bodyToMono(RefreshTokenDto.class);
        return refreshTokenDtoMono
                .flatMap(refreshTokenDto -> tokenManager.generateTokens(refreshTokenDto)
                        .flatMap(tokens -> ServerResponse
                                .ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(TokensDto
                                        .builder()
                                        .accessToken(tokens.getAccessToken())
                                        .refreshToken(tokens.getRefreshToken())
                                        .build()))))
                .onErrorResume(e -> ServerResponse
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(e.getMessage())));
    }
}

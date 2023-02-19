package pl.gozderapatryk.gatewayservice.security.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import pl.gozderapatryk.gatewayservice.security.AuthenticationManager;
import pl.gozderapatryk.gatewayservice.security.SecurityContextRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final DataBufferFactory dataBufferFactory;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        log.info("===================> WebSecurityConfig -> serverAuthenticationEntryPoint");
        return (serverWebExchange, e) -> {
            DataBuffer dataBuffer = dataBufferFactory.wrap(e.getMessage().getBytes());
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverWebExchange.getResponse().writeWith(Flux.just(dataBuffer));
        };
    }

    @Bean
    public ServerAccessDeniedHandler serverAccessDeniedHandler() {
        log.info("===================> WebSecurityConfig -> serverAccessDeniedHandler");
        return (serverWebExchange, e) -> {
            DataBuffer dataBuffer = dataBufferFactory.wrap(e.getMessage().getBytes());
            serverWebExchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return serverWebExchange.getResponse().writeWith(Flux.just(dataBuffer));
        };
    }

    @Bean
    public SecurityWebFilterChain sprSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                ).and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers("/register", "/login", "/refresh-tokens").permitAll()
                .pathMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                .anyExchange().authenticated()
                .and()
                .build();
    }
}
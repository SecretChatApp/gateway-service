package pl.gozderapatryk.gatewayservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import pl.gozderapatryk.gatewayservice.dto.RefreshTokenDto;
import pl.gozderapatryk.gatewayservice.dto.TokensDto;
import pl.gozderapatryk.gatewayservice.exception.TokenManagerException;
import pl.gozderapatryk.gatewayservice.service.UserServiceProxy;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenManager {

    @Value("${tokens.access-token.expiration-time-ms}")
    private Long accessTokenExpirationTimeMs;

    @Value("${tokens.refresh-token.expiration-time-ms}")
    private Long refreshTokenExpirationTimeMs;

    @Value("${tokens.refresh-token.property}")
    private String refreshTokenProperty;

    @Value("${tokens.prefix}")
    private String tokensPrefix;

    private final UserServiceProxy userServiceProxy;
    private final SecretKey secretKey;

    // ---------------------------------------------------------------------------------------
    // GENEROWANIE TOKENA
    // ---------------------------------------------------------------------------------------
    public Mono<TokensDto> generateTokens(User user) {

        final Date creationDate = new Date();

        final long accessTokenExpirationDateMs = System.currentTimeMillis() + accessTokenExpirationTimeMs;
        final Date accessTokenExpirationDate = new Date(accessTokenExpirationDateMs);

        final long refreshTokenExpirationDateMs = System.currentTimeMillis() + refreshTokenExpirationTimeMs;
        final Date refreshTokenExpirationDate = new Date(refreshTokenExpirationDateMs);

        return userServiceProxy
                .findByUsername(user.getUsername())
                .flatMap(userFromDb -> {
                            var accessToken = Jwts
                                    .builder()
                                    .setSubject(userFromDb.getUsername())
                                    .setIssuedAt(creationDate)
                                    .setExpiration(accessTokenExpirationDate)
                                    .signWith(secretKey)
                                    .compact();

                            var refreshToken = Jwts
                                    .builder()
                                    .setSubject(userFromDb.getUsername())
                                    .setIssuedAt(creationDate)
                                    .setExpiration(refreshTokenExpirationDate)
                                    .claim(refreshTokenProperty, accessTokenExpirationDateMs)
                                    .signWith(secretKey)
                                    .compact();

                            return Mono.just(TokensDto
                                    .builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .build());
                        }
                );
    }

    // ---------------------------------------------------------------------------------------
    // GENEROWANIE REFRESH TOKENA
    // ---------------------------------------------------------------------------------------
    public Mono<TokensDto> generateTokens(RefreshTokenDto refreshTokenDto) {

        String userId = getId(refreshTokenDto.getRefreshToken());

        final Date createdDate = new Date();

        final long accessTokenExpirationDateMs = System.currentTimeMillis() + accessTokenExpirationTimeMs;
        final Date accessTokenExpirationDate = new Date(accessTokenExpirationDateMs);

        final Date refreshTokenExpirationDate = getExpirationDate(refreshTokenDto.getRefreshToken());


        var accessToken = Jwts
                .builder()
                .setSubject(userId)
                .setIssuedAt(createdDate)
                .setExpiration(accessTokenExpirationDate)
                .signWith(secretKey)
                .compact();

        var refreshToken = Jwts
                .builder()
                .setSubject(userId)
                .setIssuedAt(createdDate)
                .setExpiration(refreshTokenExpirationDate)
                .claim(refreshTokenProperty, accessTokenExpirationDateMs)
                .signWith(secretKey)
                .compact();

        return Mono.just(TokensDto
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    // ---------------------------------------------------------------------------------------
    // PARSOWANIE TOKENA
    // ---------------------------------------------------------------------------------------
    public Claims getAllClaimsFromToken(String token) {

        if (Objects.isNull(token)) {
            throw new TokenManagerException("token is null");
        }

        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getId(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    private Date getExpirationDate(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private boolean isTokenValid(String token) {
        final Date expiration = getExpirationDate(token);
        return expiration.after(new Date());
    }

    public boolean validateToken(String token) {
        return isTokenValid(token);
    }
}

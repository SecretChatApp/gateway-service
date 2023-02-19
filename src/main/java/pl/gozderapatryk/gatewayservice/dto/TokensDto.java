package pl.gozderapatryk.gatewayservice.dto;

import lombok.*;

@Value
@Builder
public class TokensDto {
    String accessToken;
    String refreshToken;
}

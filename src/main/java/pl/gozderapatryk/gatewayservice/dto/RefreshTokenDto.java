package pl.gozderapatryk.gatewayservice.dto;

import lombok.*;

@Value
@Builder
public class RefreshTokenDto {
    String refreshToken;
}

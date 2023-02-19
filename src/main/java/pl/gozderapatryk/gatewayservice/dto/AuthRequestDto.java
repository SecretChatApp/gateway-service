package pl.gozderapatryk.gatewayservice.dto;

import lombok.*;

@Value
@Builder
public class AuthRequestDto {
    String username;
    String password;
}

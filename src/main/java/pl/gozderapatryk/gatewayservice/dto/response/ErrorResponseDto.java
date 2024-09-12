package pl.gozderapatryk.gatewayservice.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ErrorResponseDto {
    String message;
    LocalDateTime createdAt;
}

package pl.gozderapatryk.gatewayservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    String email;
    String password;
}

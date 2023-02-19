package pl.gozderapatryk.gatewayservice.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDto {
    Long id;
    String firstName;
    String lastName;
    String email;
    String username;
    String password;
}

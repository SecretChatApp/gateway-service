package pl.gozderapatryk.gatewayservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserDto {
     @NotNull
     @NotBlank
     @Email
     String email;
     @NotNull
     @NotBlank
     String emailConfirmation;
     @NotNull
     @Pattern(regexp = "[a-zA-Z0-9!@#$%^&*()-_=+]{8,40}")
     String password;
     @NotNull
     @NotBlank
     String passwordConfirmation;
}

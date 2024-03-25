package ovh.major.secure_access_system.security.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRequestDTO(

        @NotBlank(message = "Username must be not blank!")
        String name,

        @NotBlank(message = "password must be not blank!")
        String password
) {
}

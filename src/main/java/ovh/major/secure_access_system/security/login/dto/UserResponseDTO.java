package ovh.major.secure_access_system.security.login.dto;

import lombok.Builder;

@Builder
public record UserResponseDTO(
        String token,
        String name
) {
}

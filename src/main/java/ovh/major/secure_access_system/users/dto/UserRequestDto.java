package ovh.major.secure_access_system.users.dto;

import lombok.Builder;
import ovh.major.secure_access_system.users.enums.Authorities;

import java.util.Set;

@Builder
public record UserRequestDto(
        String email,
        String pass,
        Set<Authorities> authorities
) {
}

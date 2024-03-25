package ovh.major.secure_access_system.users.dto;

import lombok.Builder;
import ovh.major.secure_access_system.users.enums.Authorities;

import java.util.Set;

@Builder
public record UserDto(
        Long id,
        String email,
        String hashedPass,
        Set<Authorities> authorities
) {
}

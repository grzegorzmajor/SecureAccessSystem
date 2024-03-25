package ovh.major.secure_access_system.account.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ovh.major.secure_access_system.users.enums.Authorities;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format.")
    @Schema(example = "gal.anonim@example.com")
    private String email;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "Password must meet the specified criteria.")
    @Schema(description = "The password must contain at least 8 characters - one uppercase letter, one lowercase letter, one number and one special character", example = "Ex@mple2")
    private String password;
    @Schema(description = "Authorities: Swagger, User, Admin. Available values: SWAGGER_VIEWER, ADMIN, BASIC_USER")
    private Set<Authorities> authorities;
}

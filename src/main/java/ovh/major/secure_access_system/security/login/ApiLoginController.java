package ovh.major.secure_access_system.security.login;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ovh.major.secure_access_system.security.jwt.JwtAuthenticatorFacade;
import ovh.major.secure_access_system.security.login.dto.UserRequestDTO;
import ovh.major.secure_access_system.security.login.dto.UserResponseDTO;

@RestController
@Log4j2
@AllArgsConstructor
@Tag(name = "Login",
        description = "To request a RefreshingToken and AccessToken."
)
class ApiLoginController {

    private final JwtAuthenticatorFacade jwtAuthenticatorFacade;

    @PostMapping("/login")
    @CrossOrigin(
            origins = "*",
            methods = RequestMethod.POST
    )
    @Operation(description = "<strong>First, you need to log in to obtain a RefreshingToken (long-term).<strong>")
    public ResponseEntity<UserResponseDTO> authenticateUser(@RequestBody @Valid UserRequestDTO userRequestDTO) throws BadCredentialsException {
        final UserResponseDTO userResponse = jwtAuthenticatorFacade.authenticateAndGenerateToken(userRequestDTO);
        return ResponseEntity.ok(userResponse);
    }
}

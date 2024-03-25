package ovh.major.secure_access_system.account.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ovh.major.secure_access_system.account.dto.RegisterRequestDto;
import ovh.major.secure_access_system.exceptions_and_errors.TemplateNotFoundException;

@RestController
@RequestMapping
@Tag(name = "User Registration",
    description = "Two Steps User Account Registration"
)
@Log4j2
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(description = """
    <strong>First Step.<strong><br>Register user - this endpoint sending verification email.<br><br>
    Security requirements:<br>
    nothing<br><br>
    Body example:<br>
    {<br>
    &emsp;"email": "major.grzegorz@gmail.com",<br>
    &emsp;"password": "Ex@mple2",<br>
    &emsp;"authorities": [<br>
    &emsp;&emsp;"SWAGGER_VIEWER",<br>
    &emsp;&emsp;"ADMIN",<br>
    &emsp;&emsp;"BASIC_USER"<br>
    &emsp;]<br>
    }<br><br>
    <strong>Sometimes sending an email may take a few minutes.</strong>""")
    @PostMapping("/users/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterRequestDto registerRequestDto) throws JsonProcessingException, MessagingException, TemplateNotFoundException {
        log.info("Received self registration request");
        registrationService.registerUser(registerRequestDto);
        return ResponseEntity.ok().build();
    }
}

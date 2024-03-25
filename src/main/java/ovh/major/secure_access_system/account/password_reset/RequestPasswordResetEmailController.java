package ovh.major.secure_access_system.account.password_reset;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ovh.major.secure_access_system.account.dto.ResetPasswordRequestDto;
import ovh.major.secure_access_system.exceptions_and_errors.TemplateNotFoundException;

@RestController
@Tag(name = "Password Resetting",
    description = "Three Steps Password Reset"
)
@AllArgsConstructor
@Log4j2
public class RequestPasswordResetEmailController {

    PasswordResetService passwordResetService;

    @Operation(description = """            
    <strong>First Step.</strong><br>Request for a email with password reset link.<br><br>
    Security requirements:<br>
    nothing<br><br>
    <strong>Sometimes sending an email may take a few minutes.</strong>""")
    @PostMapping("/account/reset-password-request")
    public ResponseEntity<Void> resetPasswordRequest(@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequest) throws JsonProcessingException, TemplateNotFoundException, MessagingException {
        log.info("New password reset request.");
        passwordResetService.sendPasswordResetLink(resetPasswordRequest);
        return ResponseEntity.ok().build();
    }

}

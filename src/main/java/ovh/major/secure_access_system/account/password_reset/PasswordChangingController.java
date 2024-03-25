package ovh.major.secure_access_system.account.password_reset;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ovh.major.secure_access_system.account.dto.ResetPasswordDto;

@RestController
@Log4j2
@AllArgsConstructor
@Tag(name = "Password Resetting",
    description = "Three Steps Password Reset"
)
public class PasswordChangingController {

    PasswordResetService passwordResetService;


    @PostMapping("/account/reset-password")
    @Operation(description = """            
    <strong>Third Step.</strong><br>Request for a saving new password<br><br>
    Security requirements:<br>
    nothing<br><br>
    <strong>This endpoint should not be used in swagger. You should click on the link in the email.</strong>""")
    public ResponseEntity<Void> savePassword(@RequestBody @Valid ResetPasswordDto resetPasswordDto) throws JsonProcessingException{
        log.info("Trying to save new password...");
        passwordResetService.saveNewPassword(resetPasswordDto);
        return ResponseEntity.ok().build();
    }
}

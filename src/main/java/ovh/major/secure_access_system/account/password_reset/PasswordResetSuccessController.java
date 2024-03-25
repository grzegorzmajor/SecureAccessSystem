package ovh.major.secure_access_system.account.password_reset;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@NoArgsConstructor
@Tag(name = "Password Resetting",
        description = "Three Steps Password Reset"
)
public class PasswordResetSuccessController {
    @GetMapping("/account/password-reset-success")
    @Operation(description = "<strong>Its only success page.</strong>")
    public String showSuccess() {
        return "resetPasswordSuccess";
    }
}

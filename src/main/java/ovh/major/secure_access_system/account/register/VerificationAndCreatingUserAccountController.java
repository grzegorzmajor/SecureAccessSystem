package ovh.major.secure_access_system.account.register;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "User Registration",
    description = "Two Steps User Account Registration"
)
public class VerificationAndCreatingUserAccountController {

    private final VerificationAndCreatingUserAccountService verificationAndCreatingUserAccountService;

    @GetMapping("/verify/{token}")
    @Operation(description = """
    <strong>Second Step.</strong><br>Verify the email account with the provided token and register new user account. The result is a view based on one of the templates.<br><br>
    Security requirements:<br>
    nothing<br><br>
    <strong>This endpoint should not be used in swagger. You should click on the link in the email, but you can check by copying the token from the link in it.</strong>
    """)
    public String verifyAccount(@PathVariable("token") String token, Model model) {
        log.info("VERIFICATION ACCOUNT: Account verification received with token: " + token);
        return verificationAndCreatingUserAccountService.verify(token, model);
    }

}
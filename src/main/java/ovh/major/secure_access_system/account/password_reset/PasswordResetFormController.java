package ovh.major.secure_access_system.account.password_reset;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ovh.major.secure_access_system.account.dto.ResetPasswordRequestDto;
import ovh.major.secure_access_system.account.utils.tokens.ForEmailTokenProvider;
import ovh.major.secure_access_system.account.utils.tokens.InTokenAction;
import ovh.major.secure_access_system.exceptions_and_errors.ResourceNotFoundException;
import ovh.major.secure_access_system.exceptions_and_errors.TokenErrorException;
import ovh.major.secure_access_system.users.UserFacade;

@Controller
@Log4j2
@RequiredArgsConstructor
@Tag(name = "Password Resetting",
    description = "Three Steps Password Reset"
)
public class PasswordResetFormController {

    private final ForEmailTokenProvider tokenProvider;
    private final ObjectMapper jsonObjectMapper;
    private final UserFacade userFacade;

    @Value("${app.url}")
    private String url;


    @GetMapping("/account/reset-password-form")
    @Operation(description = """
    <strong>Second Step.</strong><br>Request for a password reset form. The result is a view based on one of the templates.<br><br>
    Security requirements:<br>
    nothing<br><br>
    <strong>This endpoint should not be used in swagger. You should click on the link in the email.</strong>""")
    public String showResetPasswordForm(@RequestParam("token") String firstToken, Model model) {
        log.info("New request for reset password form.");
        try {
            if (tokenProvider.getIssuer(firstToken)
                    .equals(InTokenAction.PASSWORD_RESETTING_REQUEST.toString())) {

                String passwordResetDetailsJson = tokenProvider.getSubjectFromToken(firstToken);
                ResetPasswordRequestDto resetPasswordRequestDto = jsonObjectMapper.readValue(passwordResetDetailsJson, ResetPasswordRequestDto.class);
                throwIfEmailNotExistInRepo(resetPasswordRequestDto);


                String newToken = tokenProvider.generateToken(passwordResetDetailsJson, InTokenAction.PASSWORD_RESETTING_FORM);
                String successLink = url + "/account/password-reset-success";

                log.info("Generated new token, and link for resetting pass endpoint (new pass saving)");

                model.addAttribute("token", newToken);
                model.addAttribute("link", successLink);

                log.info("Loading form...");

                return "resetPasswordForm";
            } else {
                throw new TokenErrorException("PASSWORD RESET FORM: Token error!");
            }

        } catch (TokenExpiredException e) {
            return "Token was expired. Please try again later.";
        } catch (TokenErrorException e) {
            return "Token error. Please try again later.";
        } catch (JsonProcessingException e) {
            return "Please try again later.";
        } catch (ResourceNotFoundException e) {
            return "There is no account with the specified email address.";
        } catch (Exception e) {
            return "Please try again later.";
        }
    }

    private void throwIfEmailNotExistInRepo(ResetPasswordRequestDto resetPasswordRequestDto) {
        if (!userFacade.isEmailExist(resetPasswordRequestDto.getEmail())) {
            throw new ResourceNotFoundException("PASSWORD RESET FORM: Error while sending email!");
        }
    }
}
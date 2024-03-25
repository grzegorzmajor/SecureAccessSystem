package ovh.major.secure_access_system.account.password_reset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ovh.major.secure_access_system.account.dto.ResetPasswordDto;
import ovh.major.secure_access_system.account.dto.ResetPasswordRequestDto;
import ovh.major.secure_access_system.exceptions_and_errors.ResourceNotFoundException;
import ovh.major.secure_access_system.exceptions_and_errors.TemplateNotFoundException;
import ovh.major.secure_access_system.exceptions_and_errors.TokenErrorException;
import ovh.major.secure_access_system.account.utils.emails.EmailService;
import ovh.major.secure_access_system.account.utils.emails.EmailTemplateLoader;
import ovh.major.secure_access_system.account.utils.tokens.ForEmailTokenProvider;
import ovh.major.secure_access_system.account.utils.tokens.InTokenAction;
import ovh.major.secure_access_system.users.UserFacade;

@Service
@RequiredArgsConstructor
@Log4j2
class PasswordResetService
{
    private final UserFacade userFacade;
    private final ObjectMapper jsonObjectMapper;
    private final ForEmailTokenProvider tokenProvider;
    private final EmailTemplateLoader emailTemplateLoader;
    private final EmailService emailService;


    @Value("${app.url}")
    private String url;

    public void sendPasswordResetLink(ResetPasswordRequestDto resetPasswordRequestDto) throws JsonProcessingException, TemplateNotFoundException, MessagingException {

        log.info("User " + resetPasswordRequestDto.getEmail() + " requesting for password reset.");

        if (userFacade.isEmailExist(resetPasswordRequestDto.getEmail())) {
            throw new ResourceNotFoundException("User who sent the password reset request does not exist");
        }
        String resetPasswordRequestDetailsJson = jsonObjectMapper.writeValueAsString(resetPasswordRequestDto);

        String token = tokenProvider.generateToken(resetPasswordRequestDetailsJson, InTokenAction.PASSWORD_RESETTING_REQUEST);

        String resetPasswordLink = url + "/account/reset-password-form?token=" + token;
        log.info("Generated link for password reset: " + resetPasswordLink);

        String htmlContentWithLink = emailTemplateLoader.load("resetPasswordEmail.html").replace("{resetPasswordLink}", resetPasswordLink);
        log.info("Email template loaded.");

        emailService.sendHtmlEmail(resetPasswordRequestDto.getEmail(), "Reset your password", htmlContentWithLink);
        log.info("An attempt was made to send an email.");

    }

    public void saveNewPassword(ResetPasswordDto resetPasswordDto) throws JsonProcessingException {
        String token = resetPasswordDto.getToken();
        if (tokenProvider.getIssuer(token)
                .equals(InTokenAction.PASSWORD_RESETTING_FORM.toString())) {
            ResetPasswordRequestDto resetPasswordRequestDto = extractObjectFromToken(token);
            throwIfEmailNotExistInRepo(resetPasswordRequestDto);
            String hashedPassword = UserFacade.encryptPassword(resetPasswordDto.getNewPassword());

            userFacade.updatePasswordByEmail(resetPasswordRequestDto.getEmail(), hashedPassword);

        } else {
            throw new TokenErrorException("PASSWORD SAVING: Token error!");
        }
    }

    private void throwIfEmailNotExistInRepo(ResetPasswordRequestDto resetPasswordRequestDto) {
        if (!userFacade.isEmailExist(resetPasswordRequestDto.getEmail())) {
            throw new ResourceNotFoundException("PASSWORD SAVING: User with specified email not exist!");
        }
    }

    private ResetPasswordRequestDto  extractObjectFromToken(String token) throws JsonProcessingException {
        String passwordResetDetailsJson = tokenProvider.getSubjectFromToken(token);
        return jsonObjectMapper.readValue(passwordResetDetailsJson, ResetPasswordRequestDto .class);
    }
}
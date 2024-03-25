package ovh.major.secure_access_system.account.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ovh.major.secure_access_system.account.dto.RegisterRequestDto;
import ovh.major.secure_access_system.exceptions_and_errors.EmailAlreadyExistsException;
import ovh.major.secure_access_system.exceptions_and_errors.TemplateNotFoundException;
import ovh.major.secure_access_system.account.utils.emails.EmailService;
import ovh.major.secure_access_system.account.utils.emails.EmailTemplateLoader;
import ovh.major.secure_access_system.account.utils.tokens.ForEmailTokenProvider;
import ovh.major.secure_access_system.account.utils.tokens.InTokenAction;
import ovh.major.secure_access_system.users.UserFacade;

@Service
@Log4j2
@RequiredArgsConstructor
class RegistrationService {

    private final UserFacade userFacade;
    private final ObjectMapper jsonObjectMapper;
    private final ForEmailTokenProvider tokenProvider;
    private final EmailService emailService;
    private final EmailTemplateLoader emailTemplateLoader;

    @Value("${app.url}")
    private String url;


    public void registerUser(RegisterRequestDto registerRequestDto) throws JsonProcessingException, MessagingException, TemplateNotFoundException {

        if (userFacade.isEmailExist(registerRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("REGISTER: Error during user registration: email already exists");
        }

        registerRequestDto.setPassword(UserFacade.encryptPassword(registerRequestDto.getPassword()));
        log.info("REGISTER: Password encryption called. Encrypted password is: " + registerRequestDto.getPassword());

        String userRegistrationDetailsJson = jsonObjectMapper.writeValueAsString(registerRequestDto);
        log.info("REGISTER: User data mapped to json string: " + userRegistrationDetailsJson);

        String token = tokenProvider.generateToken(userRegistrationDetailsJson, InTokenAction.ACCOUNT_REGISTRATION);
        log.info("REGISTER: Token with user data generating ended. Token: " + token);

        String verificationLink = url + "/account/verify/" + token;
        log.info("REGISTER: verification link generated: " + verificationLink);

        String htmlContentWithLink = emailTemplateLoader.load("activateAccountEmail.html").replace("{verificationLink}", verificationLink);
        log.info("REGISTER: email template loaded.");

        emailService.sendHtmlEmail(registerRequestDto.getEmail(), "Please verify your email account", htmlContentWithLink);
        log.info("REGISTER: email sent.");

    }

}
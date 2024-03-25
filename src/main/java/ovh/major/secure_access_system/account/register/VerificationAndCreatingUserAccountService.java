package ovh.major.secure_access_system.account.register;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ovh.major.secure_access_system.account.dto.RegisterRequestDto;
import ovh.major.secure_access_system.exceptions_and_errors.EmailAlreadyExistsException;
import ovh.major.secure_access_system.exceptions_and_errors.TokenErrorException;
import ovh.major.secure_access_system.account.utils.tokens.ForEmailTokenProvider;
import ovh.major.secure_access_system.account.utils.tokens.InTokenAction;
import ovh.major.secure_access_system.users.UserFacade;
import ovh.major.secure_access_system.users.UserMapper;
import ovh.major.secure_access_system.users.dto.UserDto;
import ovh.major.secure_access_system.users.dto.UserRequestDto;

@Service
@Log4j2
@RequiredArgsConstructor
class VerificationAndCreatingUserAccountService {

    private final UserFacade userFacade;
    private final ForEmailTokenProvider tokenProvider;
    private final ObjectMapper jsonObjectMapper;

    public String verify(String token, Model model) {
        log.info("VERIFICATION ACCOUNT: Verification method called.");
        try {
            String issuer = tokenProvider.getIssuer(token);
            log.info("VERIFICATION ACCOUNT: Issuer from received token: " + issuer);
            if (issuer.equals(InTokenAction.ACCOUNT_REGISTRATION.toString())) {
                return registerAccountByUser(token, model);
            } else {
                throw new TokenErrorException("VERIFICATION ACCOUNT: Token contains an unsupported issuer value.");
            }
        } catch (TokenExpiredException e) {
            log.info("VERIFICATION ACCOUNT: Token was expired." + e);
            String message = "Error registering an account. Token expired. Please try register again later.";
            return exceptionResolve(message,model,message,e);
        } catch (TokenErrorException e) {
            log.info("VERIFICATION ACCOUNT: Token error." + e);
            String message = "Error registering an account. Please try register again later.";
            return exceptionResolve(message,model,message,e);
        } catch (JsonProcessingException e) {
            log.info("VERIFICATION ACCOUNT: Error during json processing: " + e);
            String message = "Error registering an account. Please try register again later.";
            return exceptionResolve(message,model,message,e);
        } catch (EmailAlreadyExistsException e) {
            log.info("VERIFICATION ACCOUNT: Email already exist: " + e);
            String message = "Error registering an account. Probably you're trying verifying your email once again. Try logging in. If you can't, try registering your account again later.";
            return exceptionResolve(message,model,message,e);
        } catch (Exception e) {
            log.info("VERIFICATION ACCOUNT: Some unexpected error: " + e);
            String message = "Error registering an account. Please try register again later.";
            return exceptionResolve(message,model,message,e);
        }
    }

    private String exceptionResolve(String userErrorMessage, Model model, String loggerMessage, Exception e) {
        model.addAttribute("message", userErrorMessage);
        log.warn(this.getClass().getSimpleName() + ": " + loggerMessage + " || Exception: " + e);
        return "error";
    }

    private String registerAccountByUser(String token, Model model) throws JsonProcessingException, TokenExpiredException {
        log.info("VERIFICATION ACCOUNT: Registering user by method called.");
        RegisterRequestDto registerRequestDto = extractObjectFromToken(token, RegisterRequestDto.class);
        log.info("VERIFICATION ACCOUNT: Object from token: " + registerRequestDto.toString());
        throwIfEmailExistInRepo(registerRequestDto.getEmail());

        //password already encrypted before token generation
        UserRequestDto userRequestDto = UserMapper.fromRegisterRequestWithoutPasswordEncryption(registerRequestDto);
        log.info("VERIFICATION ACCOUNT: User to save: " + userRequestDto.toString());
        UserDto resultUserDto = userFacade.save(userRequestDto);
        log.info("VERIFICATION ACCOUNT: User saved: " + resultUserDto.toString());

        String message = "Your account has been successfully registered!";
        model.addAttribute("message", message);
        return "activationSuccess";
    }

    private void throwIfEmailExistInRepo(String email) {
        if (userFacade.isEmailExist(email)) {
            throw new EmailAlreadyExistsException("Email already exist!");
        }
    }

    private <T> T extractObjectFromToken(String token, Class<T> type) throws JsonProcessingException, TokenExpiredException {
        String userRegistrationDetailsJson = tokenProvider.getSubjectFromToken(token);
        log.info("Trying to extract user details form token...");
        return jsonObjectMapper.readValue(userRegistrationDetailsJson, type);
    }
}
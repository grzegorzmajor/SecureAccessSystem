package ovh.major.secure_access_system.account.utils.tokens;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
@Getter
class UserManagementConfiguration {

    private final int registrationExpirationTime;
    private final int passwordResetExpirationTimeFirst;
    private final int passwordResetExpirationTimeSecond;
    private final String secretKey;

    public UserManagementConfiguration(
            @Value("${userManagement.registration.linkExpirationInMinutes}")
            int registrationExpirationTime,
            @Value("${userManagement.passwordChanging.FirstToken.linkExpirationInMinutes}")
            int passwordResetExpirationTimeFirst,
            @Value("${userManagement.passwordChanging.SecondToken.linkExpirationInMinutes}")
            int passwordResetExpirationTimeSecond,
            @Value("${userManagement.secret}")
            String userManagementSecret) {

        if (userManagementSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("Secret key is too short. It should be at least 256 bits long.");
        }

        this.registrationExpirationTime = registrationExpirationTime;
        this.passwordResetExpirationTimeFirst = passwordResetExpirationTimeFirst;
        this.passwordResetExpirationTimeSecond = passwordResetExpirationTimeSecond;
        this.secretKey = userManagementSecret;
    }
}

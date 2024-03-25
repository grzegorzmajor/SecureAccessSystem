package ovh.major.secure_access_system.account.utils.tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;

@Component
@AllArgsConstructor
public class ForEmailTokenProvider {

    private final UserManagementConfiguration configuration;
    private final Clock clock;

    public String generateToken(String subject, InTokenAction issuer) {

        Algorithm algorithm = Algorithm.HMAC256(configuration.getSecretKey());
        Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = now.plus(Duration.ofMinutes(getExpirationTime(issuer)));

        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withIssuer(issuer.toString())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    private int getExpirationTime(InTokenAction issuer) {
        int expirationTime = 0;
        switch (issuer) {
            case ACCOUNT_REGISTRATION : {
                expirationTime = configuration.getRegistrationExpirationTime();
                break;
            }
            case PASSWORD_RESETTING_REQUEST : {
                expirationTime = configuration.getPasswordResetExpirationTimeFirst();
                break;
            }
            case PASSWORD_RESETTING_FORM: {
                expirationTime = configuration.getPasswordResetExpirationTimeSecond();
                break;
            }
        }
        return expirationTime;
    }

    public String getSubjectFromToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public String getIssuer(String token) {
        return JWT.decode(token).getIssuer();
    }

}

package ovh.major.secure_access_system.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.*;

@Component
@RequiredArgsConstructor
public class CreateTokenService {

    private final Clock clock;
    private final JwtRefreshingTokenConfigurationProperties refreshingProperties;
    private final JwtAccessTokenConfigurationProperties accessProperties;

    public String createToken(
            String userName,
            JwtTokenIssuer issuer) {
        Algorithm algorithm;
        Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt;
        switch (issuer) {
            case REFRESHING_TOKEN : {
                algorithm = Algorithm.HMAC256(refreshingProperties.secret());
                expiresAt = now.plus(Duration.ofMinutes(refreshingProperties.expirationMinutes()));
                break;
            }
            case ACCESS_TOKEN : {
                algorithm = Algorithm.HMAC256(accessProperties.secret());
                expiresAt = now.plus(Duration.ofMinutes(accessProperties.expirationMinutes()));
                break;
            }
            default : {
                throw new IllegalArgumentException("Invalid token issuer in JwtAuthenticatorFacade");
            }
        }

        return JWT.create()
                .withSubject(userName)
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withIssuer(issuer.getValue())
                .sign(algorithm);
    }

    public AccessTokenResponseDto createAccessToken(String userName) {
        String token = createToken(userName, JwtTokenIssuer.ACCESS_TOKEN);
        return AccessTokenResponseDto.builder()
                .accessToken(token)
                .expireDate(Timestamp.from(getExpireAt(token)))
                .userName(userName).build();
    }

    public String getTokenIssuer(String refreshedToken) {
        return JWT.decode(refreshedToken).getIssuer();
    }
    public String getTokenSubject(String refreshedToken) {
        return JWT.decode(refreshedToken).getSubject();
    }
    public Instant getExpireAt(String token) {
        return JWT.decode(token).getExpiresAt().toInstant();
    }
}

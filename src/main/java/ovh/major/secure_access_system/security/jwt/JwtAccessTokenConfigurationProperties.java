package ovh.major.secure_access_system.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "auth.jwt.access")
public record JwtAccessTokenConfigurationProperties(
        String secret,
        long expirationMinutes,
        boolean requireNotExpired // currently not used, may correspond to a different approach to token generation
) {
}

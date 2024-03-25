package ovh.major.secure_access_system.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "auth.jwt.refreshing")
public record JwtRefreshingTokenConfigurationProperties(
        String secret,
        long expirationMinutes
) {
}

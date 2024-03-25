package ovh.major.secure_access_system.security.jwt;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import ovh.major.secure_access_system.security.login.dto.UserRequestDTO;
import ovh.major.secure_access_system.security.login.dto.UserResponseDTO;

@Component
@EnableConfigurationProperties(value = {JwtRefreshingTokenConfigurationProperties.class, JwtAccessTokenConfigurationProperties.class})
public class JwtAuthenticatorFacade {
    private final AuthenticationManager authenticationManager;
    private final CreateTokenService createTokenService;

    public JwtAuthenticatorFacade(
            @Qualifier("authenticationManagerForEndpoints")
            AuthenticationManager authenticationManager, CreateTokenService createTokenService) {
        this.authenticationManager = authenticationManager;
        this.createTokenService = createTokenService;
    }

    public UserResponseDTO authenticateAndGenerateToken(UserRequestDTO userRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequestDto.name(), userRequestDto.password()));
        User user = (User) authenticate.getPrincipal();
        String name = user.getUsername();
        String token = createTokenService.createToken(name, JwtTokenIssuer.REFRESHING_TOKEN);
        return UserResponseDTO.builder()
                .token(token)
                .name(name)
                .build();
    }
}

package ovh.major.secure_access_system.security.jwt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

@RestController
@Log4j2
@AllArgsConstructor
@RequestMapping("/access_token")
@Tag(name = "Login")
class TokenRefreshController {

    private final CreateTokenService createTokenService;
    private final JwtAccessTokenConfigurationProperties jwtAccessTokenConfigurationProperties;

    @GetMapping
    @SecurityRequirement(name = "RefreshingToken")
    @Operation(description = """
            <strong>Secondly, you need to obtain an AccessToken (short-term).
            <br>This endpoint requires a RefreshingToken in the Authorization Bearer header - so enter this token by clicking the padlock on the right side.<br>
            You can obtain the RefreshingToken by logging in endpoint /login<strong>
            """)
    public ResponseEntity<AccessTokenResponseDto> getRefresh(HttpServletRequest request) {
        String authentication = request.getHeader("Authorization");
        if (jwtAccessTokenConfigurationProperties.requireNotExpired()){
            throw new MethodNotAllowedException("Method not allowed: application required not expired AccessToken - use endpoint with accessToken value.", null);
        }
        return ResponseEntity.ok(getNewToken(authentication));
    }

    @NotNull
    private AccessTokenResponseDto getNewToken(String authentication) {
        String token = authentication.substring(7);
        String issuer = createTokenService.getTokenIssuer(token);
        String userName = createTokenService.getTokenSubject(token);
        if (issuer.equals(JwtTokenIssuer.REFRESHING_TOKEN.getValue())) {
            return createTokenService.createAccessToken(userName);
        } else {
            throw new BadCredentialsException("Refreshing token error.");
        }
    }
}
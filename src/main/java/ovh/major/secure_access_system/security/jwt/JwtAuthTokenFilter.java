package ovh.major.secure_access_system.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ovh.major.secure_access_system.security.PathsForMatchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

@Component
@Log4j2
@AllArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtRefreshingTokenConfigurationProperties refreshingProperties;
    private final JwtAccessTokenConfigurationProperties accessProperties;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT AUTHORISATION TOKEN FILTER: Processing request started.");
        String path = request.getRequestURI();


        log.info("JWT: Matching path: " + path);
        if (Arrays.stream(PathsForMatchers.AUTHENTICATED_ENDPOINTS_WITH_ACCESS_TOKEN
                        .getValues())
                .anyMatch(p -> maching(p, path))) {
            log.info("JWT: Matched request to authorisation with access token.");
            String authorization = getAuthorisationBearerHeader(request);
            if (authorization == null) {
                log.warn("JWT: Authorisation not possible because no authorisation header exist in request.");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getOutputStream().println("{ \"error\": \"No token in header!\"}");
                return;
            }
            if (authorization.startsWith("Bearer ")) {
                if (!getTokenIssuer(authorization.substring(7)).equals(JwtTokenIssuer.ACCESS_TOKEN.getValue()) ) {
                    log.warn("JWT: Authorisation not possible because token has invalid issuer.");
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Invalid token!\"}");
                    return;
                }
                try {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getUsernamePasswordAuthenticationToken(authorization, JwtTokenIssuer.ACCESS_TOKEN);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                    log.info("JWT: Processing request finished successfully.");
                    return;
                } catch (TokenExpiredException e) {
                    log.info("JWT: Token expired!.");
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Token was expired!\"}");
                } catch (Exception e) {
                    log.info("JWT: Something went wrong: " + e.getLocalizedMessage());
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Something went wrong during authorisation: " + e.getLocalizedMessage() + " \"}");
                }
            }
        }

        log.info("JWT: Matching path: " + path);
        if (Arrays.stream(PathsForMatchers.AUTHENTICATED_ENDPOINTS_WITH_REFRESHING_TOKEN
                        .getValues())
                .anyMatch(p -> maching(p, path))) {
            log.info("JWT: Matched request to authorisation with access token.");
            String authorization = getAuthorisationBearerHeader(request);
            if (authorization == null) {
                log.warn("JWT: Authorisation not possible because no Authorisation Header exist in request.");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getOutputStream().println("{ \"error\": \"No token in header!\"}");
                return;
            }
            if (authorization.startsWith("Bearer ")) {
                if (!getTokenIssuer(authorization.substring(7)).equals(JwtTokenIssuer.REFRESHING_TOKEN.getValue()) ) {
                    log.warn("JWT: Authorisation not possible because token has invalid issuer.");
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Invalid token!\"}");
                    return;
                }
                try {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getUsernamePasswordAuthenticationToken(authorization, JwtTokenIssuer.REFRESHING_TOKEN);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                    log.info("JWT: Processing request finished successfully.");
                    return;
                } catch (TokenExpiredException e) {
                    log.info("JWT: Token expired!.");
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Token was expired!\"}");
                } catch (Exception e) {
                    log.info("JWT: Something went wrong: " + e.getLocalizedMessage());
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getOutputStream().println("{ \"error\": \"Something went wrong during authorisation: " + e.getLocalizedMessage() + " \"}");
                }
            }
        }
        filterChain.doFilter(request, response);
        log.info("JWT: Processing request finished.");
    }

    private static boolean maching(String p, String path) {
        boolean response = path.startsWith(p.replace("/**", ""));
        String matchedOrNot = response ? " mached" : " not mached";
        log.info(" with " + p + matchedOrNot);
        return response;
    }

    private static String getAuthorisationBearerHeader(HttpServletRequest request) {
        String authorization = null;
        Enumeration<String> authorizations = request.getHeaders("Authorization");
        while (authorizations.hasMoreElements()) {
            String authHeader = authorizations.nextElement();
            log.info("JWT: Authorisation header is: " + authHeader);
            if (authHeader.startsWith("Bearer")) {
                authorization = authHeader;
            }
        }
        return authorization;
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token, JwtTokenIssuer issuer) {
        String secretKey = switch (issuer) {
            case ACCESS_TOKEN -> accessProperties.secret();
            case REFRESHING_TOKEN -> refreshingProperties.secret();
        };
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        DecodedJWT jwt = verifier.verify(token.substring(7));
        log.info("Decoded JWT subject is: " + jwt.getSubject());
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwt.getSubject());
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        log.info("Authorities in JWT token filter: " + authorities);
        return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, authorities);
    }

    private String getTokenIssuer(String refreshingToken) {
        return JWT.decode(refreshingToken).getIssuer();
    }
}

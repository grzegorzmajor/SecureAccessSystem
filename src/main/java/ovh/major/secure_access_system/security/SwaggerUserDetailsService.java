package ovh.major.secure_access_system.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ovh.major.secure_access_system.exceptions_and_errors.NoAuthoritiesException;
import ovh.major.secure_access_system.users.UserFacade;
import ovh.major.secure_access_system.users.dto.UserDto;
import ovh.major.secure_access_system.users.enums.Authorities;

import java.util.function.Predicate;

@Service
@Log4j2
@RequiredArgsConstructor
class SwaggerUserDetailsService implements UserDetailsService {

    private final UserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        UserDto userDto = userFacade.findByEmail(username);
        if (userDto == null) {
            throw new BadCredentialsException("SWAGGER USER DETAILS SERVICE: User " + username + " not found");
        }
        if (userDto.authorities().stream().noneMatch(Predicate.isEqual(Authorities.SWAGGER_VIEWER))) {
            throw new NoAuthoritiesException("SWAGGER USER DETAILS SERVICE: User " + username + " has no authority to view Swagger.");
        }

        return User.withUsername(userDto.email())
                .password(userDto.hashedPass())
                .build();
    }
}

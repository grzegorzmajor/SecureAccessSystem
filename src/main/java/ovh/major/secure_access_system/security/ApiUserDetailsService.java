package ovh.major.secure_access_system.security;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ovh.major.secure_access_system.users.UserFacade;
import ovh.major.secure_access_system.users.dto.UserDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
@Primary
class ApiUserDetailsService implements UserDetailsService {

    private final UserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(String username) throws BadCredentialsException {
        UserDto userDTO = userFacade.findByEmail(username);
        return getUser(userDTO);
    }

    private org.springframework.security.core.userdetails.User getUser(UserDto userDto) {
        List<String> authorities = userDto.authorities()
                .stream()
                .map(Enum::toString)
                .toList();
        List<SimpleGrantedAuthority> simpleAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        log.info("LoginUserDetailsService: Logged new user. User " + userDto.email() + " has authorities " + simpleAuthorities);
        return new org.springframework.security.core.userdetails.User(
                userDto.email(),
                userDto.hashedPass(),
                simpleAuthorities);
    }
}

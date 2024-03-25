package ovh.major.secure_access_system.users;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import ovh.major.secure_access_system.exceptions_and_errors.ResourceNotFoundException;
import ovh.major.secure_access_system.users.dto.UserDto;
import ovh.major.secure_access_system.users.dto.UserRequestDto;
import ovh.major.secure_access_system.users.enums.Authorities;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public static String encryptPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public UserDto save(UserRequestDto userRequestDto) {
        return userService.save(userRequestDto);
    }

    public UserDto findById(Long id) {
        UserDto userDto = userService.findById(id);
        if (userDto==null) {
            throw new ResourceNotFoundException("USERS: User with id " + id + " not found!");
        }
        return userDto;
    }

    public UserDto findByEmail(String email) {
        UserDto userDto = userService.findByEmail(email);
        if (userDto==null) {
            throw new ResourceNotFoundException("USERS: User with email " + email + " not found!");
        }
        return userDto;
    }

    public List<UserDto> findWithSpecifiedAuthority(Authorities authority) {
        List<UserDto> usersDto = userService.findWithSpecifiedAuthority(authority.toString());
        if (usersDto==null) {
            throw new ResourceNotFoundException("USERS: No users found with authority " + authority);
        }
        return usersDto;
    }

    public boolean isEmailExist(String email) {
        //todo isEmailExist
        return false;
    }

    public void updatePasswordByEmail(String email, String hashedPassword) {
        //todo updatePasswordByEmail
    }
}

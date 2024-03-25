package ovh.major.secure_access_system.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ovh.major.secure_access_system.users.dto.UserDto;
import ovh.major.secure_access_system.users.dto.UserRequestDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserService {
    private final UserRepository userRepository;

    UserDto save(UserRequestDto userRequestDto) {
        User user = UserMapper.fromUserRequestDto(userRequestDto);
        User userResult  = userRepository.save(user);
        return UserMapper.toUserDto(userResult);
    }

    UserDto findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::toUserDto)
                .orElse(null);
    }

    UserDto findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper::toUserDto)
                .orElse(null);
    }

    public List<UserDto> findWithSpecifiedAuthority(String authority) {
        List<User> users = userRepository.findByAuthorities(authority);
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }
}

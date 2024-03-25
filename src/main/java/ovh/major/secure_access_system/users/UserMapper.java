package ovh.major.secure_access_system.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ovh.major.secure_access_system.account.dto.RegisterRequestDto;
import ovh.major.secure_access_system.users.dto.UserDto;
import ovh.major.secure_access_system.users.dto.UserRequestDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public static UserRequestDto fromRegisterRequestWithoutPasswordEncryption(RegisterRequestDto registerRequest) {
        return UserRequestDto.builder()
                .email(registerRequest.getEmail())
                .pass(registerRequest.getPassword())
                .authorities(registerRequest.getAuthorities())
                .build();
    }

    static UserDto toUserDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .hashedPass(u.getHashedPass())
                .authorities(u.getAuthorities())
                .build();
    }

    static User fromUserRequestDto(UserRequestDto userRequestDto) {
        return User.builder()
                .email(userRequestDto.email())
                .authorities(userRequestDto.authorities())
                .hashedPass(userRequestDto.pass())
                .build();
    }

    private static String formatDataToSimpleFormat(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    private static Date getDateFromSimpleFormat(String simpleDate) throws ParseException {
        if (simpleDate == null || simpleDate.isBlank()) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.parse(simpleDate);
    }

}
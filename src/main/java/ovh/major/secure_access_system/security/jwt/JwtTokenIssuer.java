package ovh.major.secure_access_system.security.jwt;

import lombok.Getter;

@Getter
public enum JwtTokenIssuer {
    ACCESS_TOKEN("LogToLife:AccessToken"),
    REFRESHING_TOKEN("LogToLife:RefreshingToken");

    private final String value;

    JwtTokenIssuer(String value) {
        this.value = value;
    }

}

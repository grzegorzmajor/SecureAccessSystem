package ovh.major.secure_access_system.security;

import lombok.Getter;

@Getter
public enum PathsForMatchers {
    SWAGGER_ALL(new String[]{
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/index.html",
            "/swagger-ui/**",
            "/webjars/**"}),
    OPENED_ENDPOINTS(new String[]{
            "/login",
            "/test/opened"}),
    AUTHENTICATED_ENDPOINTS_WITH_REFRESHING_TOKEN(new String[]{"/access_token"}),
    AUTHENTICATED_ENDPOINTS_WITH_ACCESS_TOKEN(new String[]{
            "/test/user",
            "/test/admin"}),
    ALL_AUTHENTICATED_ENDPOINTS(new String[]{
            "/test/user",
            "/test/admin",
            "/access_token"});

    private final String[] values;

    PathsForMatchers(String[] values) {
        this.values = values;
    }
}

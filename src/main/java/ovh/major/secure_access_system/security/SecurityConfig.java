package ovh.major.secure_access_system.security;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import ovh.major.secure_access_system.security.jwt.JwtAccessTokenConfigurationProperties;
import ovh.major.secure_access_system.security.jwt.JwtAuthTokenFilter;
import ovh.major.secure_access_system.security.jwt.JwtRefreshingTokenConfigurationProperties;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = false)
@Log4j2
@AllArgsConstructor
class SecurityConfig  {

    private final SwaggerUserDetailsService swaggerUserDetailsService;
    private final ApiUserDetailsService apiUserDetailsService;
    private final JwtRefreshingTokenConfigurationProperties refreshingProperties;
    private final JwtAccessTokenConfigurationProperties accessProperties;

    @Bean("authenticationManagerForSwagger")
    @Primary
    public AuthenticationManager authenticationManagerForSwagger() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(swaggerUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean("authenticationManagerForEndpoints")
    public AuthenticationManager authenticationManagerForEndpoints() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(apiUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    @Primary
//    public UserDetailsService userDetailsServiceForEndpoints(ApiLoginFacade loginFacade) {
//        return new ApiUserDetailsService(loginFacade);
//    }

    @Order(1)
    @Bean
    public SecurityFilterChain basicSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Basic security filter chain (for swagger only) LOADED");
        httpSecurity
                .securityMatcher(PathsForMatchers.SWAGGER_ALL.getValues())
                .authenticationManager(authenticationManagerForSwagger())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(PathsForMatchers.SWAGGER_ALL.getValues()).authenticated())
                .httpBasic(Customizer.withDefaults())
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Order(3)
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("JWT security filter chain (for all endpoints requiring authorization) LOADED");
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(PathsForMatchers.ALL_AUTHENTICATED_ENDPOINTS.getValues())
                .authenticationManager(authenticationManagerForEndpoints())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(PathsForMatchers.ALL_AUTHENTICATED_ENDPOINTS.getValues()).authenticated())
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(Customizer.withDefaults())
                .addFilterAfter(getJwtFilter(),
                        ExceptionTranslationFilter.class);
        return httpSecurity.build();
    }
    @Order(2)
    @Bean
    public SecurityFilterChain openedEndpoitnsSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Security filter chain for all endpoints that do not require authorization LOADED");
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(PathsForMatchers.OPENED_ENDPOINTS.getValues())
                .authorizeHttpRequests(authorize ->
                        authorize.requestMatchers(PathsForMatchers.OPENED_ENDPOINTS.getValues()).permitAll())
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(Customizer.withDefaults());
        return httpSecurity.build();
    }


    private JwtAuthTokenFilter getJwtFilter() {
        return new JwtAuthTokenFilter(
                refreshingProperties,
                accessProperties,
                apiUserDetailsService);
    }
}

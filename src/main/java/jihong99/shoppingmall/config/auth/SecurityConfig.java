package jihong99.shoppingmall.config.auth;

import jihong99.shoppingmall.config.auth.filters.CsrfCookieFilter;
import jihong99.shoppingmall.config.auth.filters.JwtAuthenticationFilter;
import jihong99.shoppingmall.config.auth.providers.UsernamePwdAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static jihong99.shoppingmall.entity.enums.Roles.*;
import static jihong99.shoppingmall.entity.enums.Roles.SUPER_ADMIN;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CsrfCookieFilter csrfCookieFilter;
    private final UsernamePwdAuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain for the application.
     *
     * <p>This method sets up the security context, session management, CORS, CSRF, and request authorization configurations.
     * It also adds custom filters for CSRF and JWT authentication.</p>
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain object
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        http
                .securityContext(httpSecuritySecurityContextConfigurer -> httpSecuritySecurityContextConfigurer
                        .requireExplicitSave(false))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfig.corsConfigurationSource()))
                .csrf(csrfConfigurer -> csrfConfigurer.csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/api/signup", "/api/users/check-id", "/api/login", "/h2-console/**", "/api/refresh-token") // public API urls
                        .csrfTokenRepository(csrfTokenRepository))
                .addFilterAfter(csrfCookieFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/signup", "/api/users/check-id", "/api/login", "/h2-console/**", "/api/refresh-token","/api/csrf-token", "/api/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole(USER.name(), ADMIN.name(), SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/logout", "/api/users/**").hasAnyRole(USER.name(), ADMIN.name(), SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole(USER.name(), ADMIN.name(), SUPER_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole(USER.name(), ADMIN.name(), SUPER_ADMIN.name())
                        .requestMatchers("/api/admin/**").hasRole(ADMIN.name())
                        .requestMatchers("/api/super-admin/**").hasRole(SUPER_ADMIN.name()))
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())); // access h2 console
        return http.build();
    }

    /**
     * Creates an AuthenticationManager bean.
     *
     * <p>This method sets up an AuthenticationManager with a custom UsernamePwdAuthenticationProvider.</p>
     *
     * @return the AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider);
    }
}

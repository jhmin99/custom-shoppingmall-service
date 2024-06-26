package jihong99.shoppingmall.config.auth;

import jihong99.shoppingmall.config.auth.filters.CsrfCookieFilter;
import jihong99.shoppingmall.config.auth.filters.JwtAuthenticationFilter;
import jihong99.shoppingmall.config.auth.providers.UsernamePwdAuthenticationProvider;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CsrfCookieFilter csrfCookieFilter;

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

        http
                .securityContext(httpSecuritySecurityContextConfigurer -> httpSecuritySecurityContextConfigurer
                        .requireExplicitSave(false))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfig.corsConfigurationSource()))
                .csrf(csrfConfigurer -> csrfConfigurer.csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/api/signup", "/api/users/check-id", "/api/login", "/h2-console/**", "/api/refresh-token") // public API urls
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .addFilterAfter(csrfCookieFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/logout").authenticated()
                        .requestMatchers("/api/signup", "/api/users/check-id", "/api/login", "/h2-console/**", "/api/refresh-token").permitAll())
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable())); // access h2 console
        return http.build();
    }

    /**
     * Creates a PasswordEncoder bean.
     *
     * <p>This method returns a BCryptPasswordEncoder to be used for encoding passwords.</p>
     *
     * @return the PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
        UsernamePwdAuthenticationProvider authenticationProvider = new UsernamePwdAuthenticationProvider(userRepository, passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}

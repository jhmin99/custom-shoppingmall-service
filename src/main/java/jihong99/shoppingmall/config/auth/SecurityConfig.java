package jihong99.shoppingmall.config.auth;
import jihong99.shoppingmall.config.auth.filters.CustomCsrfCookieFilter;
import jihong99.shoppingmall.config.auth.providers.CustomUsernamePwdAuthenticationProvider;
import jihong99.shoppingmall.repository.UserRepository;
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

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CorsConfig corsConfig;
    private UserRepository userRepository;

    public SecurityConfig(CorsConfig corsConfig, UserRepository userRepository) {
        this.corsConfig = corsConfig;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .securityContext((context) -> context
                        .requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .csrf(csrfConfigurer -> csrfConfigurer.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/api/users","/api/users/check-id","/api/login")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                        .addFilterAfter(new CustomCsrfCookieFilter(), BasicAuthenticationFilter.class)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfig.corsConfigurationSource()))
                .authorizeHttpRequests(requests -> requests
                                .requestMatchers(HttpMethod.GET, "/api/users/**").authenticated()
                                .requestMatchers("/resources/**").denyAll()
                                .requestMatchers("/**").permitAll()
                        )
                .headers(headers -> headers.frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))) // access h2 console
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        CustomUsernamePwdAuthenticationProvider authenticationProvider = new CustomUsernamePwdAuthenticationProvider(userRepository, passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}

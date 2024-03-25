package jihong99.shoppingmall.config.auth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CorsConfig config;

    public SecurityConfig(CorsConfig config) {
        this.config = config;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                securityContext(
                (context) -> context.requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf((csrfConfigurer -> csrfConfigurer.disable()))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(config.corsConfigurationSource()))
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

}

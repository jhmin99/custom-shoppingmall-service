package jihong99.shoppingmall.config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import static org.springframework.security.config.Customizer.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public WebSecurityConfig(@Qualifier("corsConfigurationSource") CorsConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    private CorsConfigurationSource configurationSource;

    /**
     * @Class HttpSecurity docs:
     * <p>https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html#headers(org.springframework.security.config.Customizer)
     * </p>
     **/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityContext((context) -> context
                .requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .csrf((csrfConfigurer -> csrfConfigurer.disable()))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(configurationSource))
                .authorizeHttpRequests(authourize -> authourize
                        .requestMatchers("/**").permitAll())
                .headers(headers -> headers.frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))) // access h2 console
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

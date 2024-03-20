package jihong99.shoppingmall.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public WebSecurityConfig(@Qualifier("corsConfigurationSource") CorsConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    private CorsConfigurationSource configurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityContext((context) -> context
                .requireExplicitSave(false))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .csrf((csrfConfigurer -> csrfConfigurer.disable()))
                .cors(corsCustomizer -> corsCustomizer.configurationSource(configurationSource))
                .authorizeHttpRequests(authourize -> authourize
                        .requestMatchers("/**").permitAll())
                .headers(headers -> headers.frameOptions().disable()) // access h2 console
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

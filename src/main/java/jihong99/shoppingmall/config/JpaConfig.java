package jihong99.shoppingmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * Configuration class for enabling JPA Auditing.
 *
 * <p>This configuration class enables JPA Auditing for the application.
 * JPA Auditing allows for automatic population of auditing fields like
 * created date, last modified date, created by, and last modified by
 * in entity classes.</p>
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

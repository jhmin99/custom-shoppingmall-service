package jihong99.shoppingmall.config.auth.providers;

import jihong99.shoppingmall.dto.UserDetailsDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates the user based on username and password.
     *
     * <p>This method retrieves the user from the repository using the provided username, then checks if the provided
     * password matches the stored password using the password encoder. If the credentials are valid, it returns an
     * authentication token containing the user's details. If the credentials are invalid, it throws a
     * BadCredentialsException.</p>
     *
     * @param authentication The authentication request object containing the username and password
     * @return A fully authenticated object including credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Users user = userRepository.findByIdentification(username)
                .orElseThrow(() -> new BadCredentialsException("No user registered with this details!"));

        if(passwordEncoder.matches(password, user.getPassword())){
            UserDetailsDto userDetailsDto = new UserDetailsDto(user);
            return new UsernamePasswordAuthenticationToken(userDetailsDto, password, userDetailsDto.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
    }

    /**
     * Checks if this provider supports the given authentication type.
     *
     * <p>This method returns true if the authentication type is UsernamePasswordAuthenticationToken.</p>
     *
     * @param authentication The class of the authentication object
     * @return true if this authentication provider supports the specified authentication object
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

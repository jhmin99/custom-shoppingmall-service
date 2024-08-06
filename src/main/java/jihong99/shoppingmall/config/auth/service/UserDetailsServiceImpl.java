package jihong99.shoppingmall.config.auth.service;

import jihong99.shoppingmall.config.auth.UserDetailsDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Loads the user details based on the given username (identification).
     *
     * <p>This method retrieves the user from the repository using the provided identification. If the user is found,
     * it returns a UserDetailsDto containing the user's details. If the user is not found, it throws a
     * UsernameNotFoundException.</p>
     *
     * @param identification The username or identification of the user to load
     * @return A UserDetails object containing the user's information
     * @throws UsernameNotFoundException if the user cannot be found
     */
    @Override
    public UserDetails loadUserByUsername(String identification) throws UsernameNotFoundException {
        Users user = userRepository.findByIdentification(identification)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identification: " + identification));
        return new UserDetailsDto(user);
    }
}

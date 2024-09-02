package jihong99.shoppingmall.service;

import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.InvalidTokenException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static jihong99.shoppingmall.constants.Constants.*;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken The refresh token provided by the client.
     * @return A map containing the new access token.
     * @throws InvalidTokenException If the refresh token is invalid.
     * @throws NotFoundException If no user is found with the identification extracted from the refresh token.
     */
    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException(MESSAGE_400_InvalidRefreshToken);
        }

        String identification = jwtTokenProvider.getIdentificationFromToken(refreshToken);
        Users user = findUserOrThrow(identification);

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return response;
    }


    /**
     * Checks if the authenticated user has the specified user ID.
     *
     * <p>This method retrieves the currently authenticated user's identification,
     * looks up the user in the database, and checks if the user's ID matches the specified ID.</p>
     *
     * @param userId The user ID to check against the authenticated user's ID.
     * @return true if the authenticated user's ID matches the specified ID, false otherwise.
     * @throws NotFoundException If no user is found with the authenticated user's identification.
     */
    @Override
    public boolean hasId(Long userId) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
        return user.getIdentification().equals(name);
    }



    private Users findUserOrThrow(String identification) {
        return userRepository.findByIdentification(identification)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }
}

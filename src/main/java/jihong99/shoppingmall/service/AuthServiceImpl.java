package jihong99.shoppingmall.service;

import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


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
     * @throws IllegalArgumentException If the refresh token is invalid.
     * @throws NotFoundException If no user is found with the identification extracted from the refresh token.
     */
    @Override
    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String identification = jwtTokenProvider.getIdentificationFromToken(refreshToken);
        Users user = userRepository.findByIdentification(identification)
                .orElseThrow(() -> new NotFoundException("User not found with identification: " + identification));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return response;
    }
}

package jihong99.shoppingmall.service;

import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the successful refresh of an access token.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    void refreshAccessToken_Success() {
        // given
        String refreshToken = "validRefreshToken";
        String identification = "testuser";
        String newAccessToken = "newAccessToken";

        Users user = Users.builder()
                .identification(identification)
                .build();

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getIdentificationFromToken(refreshToken)).thenReturn(identification);
        when(userRepository.findByIdentification(identification)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(user)).thenReturn(newAccessToken);

        // when
        var response = authService.refreshAccessToken(refreshToken);

        // then
        assertThat(response).isNotNull();
        assertThat(response.get("accessToken")).isEqualTo(newAccessToken);

        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
        verify(jwtTokenProvider, times(1)).getIdentificationFromToken(refreshToken);
        verify(userRepository, times(1)).findByIdentification(identification);
        verify(jwtTokenProvider, times(1)).generateAccessToken(user);
    }

    /**
     * Tests handling of an IllegalArgumentException when the refresh token is invalid.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    void refreshAccessToken_IllegalArgumentException_No_RefreshToken() {
        // given
        String invalidRefreshToken = null;

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshAccessToken(invalidRefreshToken);
        });

        verify(jwtTokenProvider, never()).validateToken(invalidRefreshToken);
        verify(jwtTokenProvider, never()).getIdentificationFromToken(anyString());
        verify(userRepository, never()).findByIdentification(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any(Users.class));
    }

    /**
     * Tests handling of an IllegalArgumentException when the refresh token is invalid.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    void refreshAccessToken_IllegalArgumentException_Invalid_RefreshToken() {
        // given
        String invalidRefreshToken = "invalidRefreshToken";

        when(jwtTokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            authService.refreshAccessToken(invalidRefreshToken);
        });

        verify(jwtTokenProvider, times(1)).validateToken(invalidRefreshToken);
        verify(jwtTokenProvider, never()).getIdentificationFromToken(anyString());
        verify(userRepository, never()).findByIdentification(anyString());
        verify(jwtTokenProvider, never()).generateAccessToken(any(Users.class));
    }
    /**
     * Tests handling of a UserNotFoundException when the user associated with the token is not found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    void refreshAccessToken_UserNotFoundException() {
        // given
        String refreshToken = "validRefreshToken";
        String identification = "nonexistentuser";

        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getIdentificationFromToken(refreshToken)).thenReturn(identification);
        when(userRepository.findByIdentification(identification)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> {
            authService.refreshAccessToken(refreshToken);
        });

        verify(jwtTokenProvider, times(1)).validateToken(refreshToken);
        verify(jwtTokenProvider, times(1)).getIdentificationFromToken(refreshToken);
        verify(userRepository, times(1)).findByIdentification(identification);
        verify(jwtTokenProvider, never()).generateAccessToken(any(Users.class));
    }
}

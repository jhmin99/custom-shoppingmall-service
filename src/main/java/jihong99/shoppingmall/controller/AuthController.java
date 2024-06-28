package jihong99.shoppingmall.controller;

import jakarta.servlet.http.HttpServletRequest;
import jihong99.shoppingmall.exception.UserNotFoundException;
import jihong99.shoppingmall.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This controller handles authentication-related endpoints, including refreshing the access token
 * and retrieving the CSRF token.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    private final IAuthService authService;
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * Endpoint to refresh the access token using a provided refresh token.
     *
     * @param request A map containing the refresh token.
     * @return A ResponseEntity containing the new access token or an error status.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            Map<String, String> response = authService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Endpoint to retrieve the CSRF token for the current session.
     *
     * @param request The HttpServletRequest object.
     * @return The CSRF token.
     */
    @GetMapping("/csrf-token")
    public CsrfToken csrfToken(HttpServletRequest request) {
        LOGGER.info(request.getAttribute(CsrfToken.class.getName()).toString());
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}

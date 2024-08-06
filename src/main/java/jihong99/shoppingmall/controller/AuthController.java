package jihong99.shoppingmall.controller;

import jakarta.servlet.http.HttpServletRequest;
import jihong99.shoppingmall.exception.InvalidTokenException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final IAuthService iauthService;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    /**
     * Refresh the access token.
     *
     * <p>This endpoint allows a user to refresh their access token using a valid refresh token.
     * The request body must contain the refresh token.</p>
     *
     * @param request A map containing the refresh token
     * @return ResponseEntity<Map<String, String>> Response object containing the new access token
     * @success Access token successfully refreshed
     * Response Code: 200
     * @exception InvalidTokenException Thrown if the refresh token is invalid
     * Response Code: 400
     * @exception NotFoundException Thrown if the user associated with the refresh token is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, String> response = iauthService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to retrieve the CSRF token for the current session.
     *
     * @param request The HttpServletRequest object.
     * @return The CSRF token.
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/csrf-token")
    public CsrfToken csrfToken(HttpServletRequest request) {
        LOGGER.info(request.getAttribute(CsrfToken.class.getName()).toString());
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}

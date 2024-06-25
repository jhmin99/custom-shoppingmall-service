package jihong99.shoppingmall.config.auth.providers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jihong99.shoppingmall.entity.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${security.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    /**
     * Generates an access token for the specified user.
     *
     * <p>This method creates a JWT access token containing the user's ID and identification. The token is
     * signed with the secret key and has an expiration time set by the application properties.</p>
     *
     * @param user The user for whom the access token is to be generated
     * @return The generated JWT access token as a string
     */
    public String generateAccessToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("identification", user.getIdentification());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getIdentification())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Generates a refresh token for the specified user.
     *
     * <p>This method creates a JWT refresh token containing the user's identification. The token is signed
     * with the secret key and has an expiration time set by the application properties.</p>
     *
     * @param user The user for whom the refresh token is to be generated
     * @return The generated JWT refresh token as a string
     */
    public String generateRefreshToken(Users user) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getIdentification())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Validates the specified JWT token.
     *
     * <p>This method verifies the signature and expiration of the JWT token using the secret key.</p>
     *
     * @param token The JWT token to be validated
     * @return True if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extracts the user's identification from the specified JWT token.
     *
     * <p>This method parses the JWT token to extract the subject (user's identification) using the secret key.</p>
     *
     * @param token The JWT token from which the identification is to be extracted
     * @return The user's identification extracted from the token
     */
    public String getIdentificationFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}

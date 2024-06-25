package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String statusCode;
    private String statusMessage;
    private String accessToken;
    private String refreshToken;
    private Long userId;
}

package jihong99.shoppingmall.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Long userId;

    public static LoginResponseDto of(String accessToken, String refreshToken, Long userId){
        return new LoginResponseDto(accessToken,refreshToken,userId);
    }

}

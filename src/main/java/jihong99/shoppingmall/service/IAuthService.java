package jihong99.shoppingmall.service;

import java.util.Map;

public interface IAuthService {
    Map<String, String> refreshAccessToken(String refreshToken);

}

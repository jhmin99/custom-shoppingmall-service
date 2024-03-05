package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.SignUpDto;

public interface IUserService {

    void signUpAccount(SignUpDto signUpDto);

    boolean isIdentificationVerified(String identification);

    boolean checkDuplicateIdentification(String identification);

    boolean matchPassword(String password, String confirmPassword);


}

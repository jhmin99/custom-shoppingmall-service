package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.SignUpDto;

public interface IUserService {

    void signUpAccount(SignUpDto signUpDto);

    void checkDuplicateIdentification(String identification);

}

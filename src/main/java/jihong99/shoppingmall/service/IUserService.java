package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.dto.UserDetailsDto;

public interface IUserService {

    void signUpAccount(SignUpDto signUpDto);

    void checkDuplicateIdentification(String identification);

    UserDetailsDto getUserDetails(Long id);

}

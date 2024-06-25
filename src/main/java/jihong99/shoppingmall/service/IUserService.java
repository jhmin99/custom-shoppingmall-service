package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.LoginRequestDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;

public interface IUserService {

    void signUpAccount(SignUpDto signUpDto);

    void checkDuplicateIdentification(String identification);


    Users loginByIdentificationAndPassword(LoginRequestDto loginRequestDto);

    String generateAccessToken(Users user);

    String generateRefreshToken(Users user);

}

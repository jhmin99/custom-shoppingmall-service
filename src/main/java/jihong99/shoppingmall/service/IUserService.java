package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.request.LoginRequestDto;
import jihong99.shoppingmall.dto.request.PatchAdminRequestDto;
import jihong99.shoppingmall.dto.request.PatchUserRequestDto;
import jihong99.shoppingmall.dto.response.AdminSummaryResponseDto;
import jihong99.shoppingmall.dto.response.UserDetailsResponseDto;
import jihong99.shoppingmall.dto.request.SignUpRequestDto;
import jihong99.shoppingmall.dto.response.UserSummaryResponseDto;
import jihong99.shoppingmall.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    void signUpAccount(SignUpRequestDto signUpRequestDto);

    void checkDuplicateIdentification(String identification);


    Users loginByIdentificationAndPassword(LoginRequestDto loginRequestDto);

    String generateAccessToken(Users user);

    String generateRefreshToken(Users user);

    UserDetailsResponseDto getUserDetails(Long userId);

    void signUpAdminAccount(SignUpRequestDto signUpRequestDto);
    Page<UserSummaryResponseDto> getUsers(Pageable pageable);

    Page<AdminSummaryResponseDto> getAdmins(Pageable pageable);

    void patchAdminAccount(Long id, PatchAdminRequestDto patchAdminRequestDto);
    void deleteAdminAccount(Long id);

    void patchUserAccount(Long id, PatchUserRequestDto patchUserRequestDto);
}

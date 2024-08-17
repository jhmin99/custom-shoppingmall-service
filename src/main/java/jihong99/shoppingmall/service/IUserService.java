package jihong99.shoppingmall.service;
import jihong99.shoppingmall.dto.request.auth.LoginRequestDto;
import jihong99.shoppingmall.dto.request.user.PatchAdminRequestDto;
import jihong99.shoppingmall.dto.request.user.PatchUserRequestDto;
import jihong99.shoppingmall.dto.response.user.AdminSummaryResponseDto;
import jihong99.shoppingmall.dto.response.user.UserDetailsResponseDto;
import jihong99.shoppingmall.dto.request.auth.SignUpRequestDto;
import jihong99.shoppingmall.dto.response.user.UserSummaryResponseDto;
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

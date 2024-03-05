package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Cart;
import jihong99.shoppingmall.entity.User;
import jihong99.shoppingmall.entity.WishList;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.mapper.UserMapper;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static jihong99.shoppingmall.entity.enums.Tier.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService{

    private UserRepository userRepository;

    @Override
    public void signUpAccount(SignUpDto signUpDto) {
        if(!matchPassword(signUpDto.getPassword(), signUpDto.getConfirmPassword())){
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }else{
            User user = UserMapper.mapToUser(signUpDto);
            // 회원 생성 시 장바구니 및 찜 함께 생성
            createCartAndWishList(user);
            // 회원 부가정보 추가 생성
            createAdditionalUserInfo(user);

            userRepository.save(user);
        }

    }

    // 객체 User에 종속되어 있으므로 static은 적합하지 않음
    private void createAdditionalUserInfo(User user) {
        user.updatePoint(0);
        user.updateTier(IRON);
        user.updateAmountToNextTier(50000);
    }

    private void createCartAndWishList(User user) {
        Cart cart = new Cart();
        WishList wishList = new WishList();

        user.createCart(cart);
        user.createWishList(wishList);
    }

    @Override
    public boolean isIdentificationVerified(String identification) {
        boolean isVerified = false;
        if(checkDuplicateIdentification(identification)){
            throw new DuplicateIdentificationException("중복된 아이디가 존재합니다.");
        }
        isVerified = true;
        return isVerified;
    }

    @Override
    public boolean checkDuplicateIdentification(String identification) {
        return userRepository.findByIdentification(identification).isPresent();
    }

    @Override
    public boolean matchPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


}

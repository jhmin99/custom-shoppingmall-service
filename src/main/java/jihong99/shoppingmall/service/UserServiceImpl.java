package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Cart;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.WishList;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.mapper.UserMapper;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeParseException;

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
            UserMapper userMapper = new UserMapper();
            try{
                Users users = userMapper.mapToUser(signUpDto);
                // 회원 생성 시 장바구니 및 찜 함께 생성
                createCartAndWishList(users);
                // 회원 부가정보 추가 생성
                createAdditionalUserInfo(users);

                userRepository.save(users);
            }catch (DateTimeParseException e){
                throw new DateTimeParseException("생년월일이 올바르지 않습니다.", signUpDto.getBirthDate(),
                        e.getErrorIndex());
            }
        }

    }

    // 객체 User에 종속되어 있으므로 static은 적합하지 않음
    private void createAdditionalUserInfo(Users users) {
        users.updatePoint(0);
        users.updateTier(IRON);
        users.updateAmountToNextTier(50000);
    }

    private void createCartAndWishList(Users users) {
        Cart cart = new Cart();
        WishList wishList = new WishList();

        users.createCart(cart);
        users.createWishList(wishList);
    }

    @Override
    public void checkDuplicateIdentification(String identification) {
        if(isIdentificationExist(identification)){
            throw new DuplicateIdentificationException("중복된 아이디가 존재합니다.");
        }

    }

    @Override
    public boolean isIdentificationExist(String identification) {
        return userRepository.findByIdentification(identification).isPresent();
    }

    @Override
    public boolean matchPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }


}

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

    /**
     * Register a new user.
     *
     * <p>
     * This method processes the registration of a new user by storing the provided user information
     * and creating a shopping cart, wishlist, and additional user information.
     *
     * </p>
     * @param signUpDto The DTO object containing the necessary information for user registration
     *
     * @throws PasswordMismatchException Thrown if the passwords provided do not match
     * @throws DateTimeParseException Thrown if the birth date format is invalid
     */
    @Override
    public void signUpAccount(SignUpDto signUpDto) {
        if(!matchPassword(signUpDto.getPassword(), signUpDto.getConfirmPassword())){
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }else{
            UserMapper userMapper = new UserMapper();
            try{
                Users users = userMapper.mapToUser(signUpDto);
                createCartAndWishList(users);
                createAdditionalUserInfo(users);

                userRepository.save(users);
            }catch (DateTimeParseException e){
                throw new DateTimeParseException("생년월일이 올바르지 않습니다.", signUpDto.getBirthDate(),
                        e.getErrorIndex());
            }
        }

    }

    /**
     * Checks for duplicate identification.
     *
     * <p>This method checks if the provided identification already exists in the repository.</p>
     *
     * @param identification
     *
     * @throws DuplicateIdentificationException Thrown if the given identification already exists
     */
    @Override
    public void checkDuplicateIdentification(String identification) {
        if(isIdentificationExist(identification)){
            throw new DuplicateIdentificationException("중복된 아이디가 존재합니다.");
        }
    }


    /**
     * @param users
     * Generate additional user information
     */
    private void createAdditionalUserInfo(Users users) {
        users.updatePoint(0);
        users.updateTier(IRON);
        users.updateAmountToNextTier(50000);
    }

    /**
     * @param users
     * Create a shopping cart and wishlist simultaneously upon user creation
     */
    private void createCartAndWishList(Users users) {
        Cart cart = new Cart(0L);
        WishList wishList = new WishList();

        users.updateCart(cart);
        users.updateWishList(wishList);
    }

    /**
     *
     * @param identification
     * @return true if the given identification already exists in the userRepository, false otherwise
     */
    private boolean isIdentificationExist(String identification) {
        return userRepository.findByIdentification(identification).isPresent();
    }

    /**
     *
     * @param password
     * @param confirmPassword
     * @return true if two passwords match, false otherwise
     */
    private boolean matchPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}

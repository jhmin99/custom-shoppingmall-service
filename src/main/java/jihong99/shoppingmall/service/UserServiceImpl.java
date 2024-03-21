package jihong99.shoppingmall.service;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Cart;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.WishList;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.mapper.UserMapper;
import jihong99.shoppingmall.repository.CartRepository;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.repository.WishListRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static jihong99.shoppingmall.entity.enums.Tiers.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService{

    private UserRepository userRepository;
    private CartRepository cartRepository;
    private WishListRepository wishListRepository;
    private PasswordEncoder passwordEncoder;

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
     * @throws DuplicateIdentificationException Thrown if the identification already exists
     * @throws PasswordMismatchException Thrown if the passwords provided do not match
     * @throws DateTimeParseException Thrown if the birth date format is invalid
     */
    @Override
    @Transactional
    public void signUpAccount(SignUpDto signUpDto) {
        Optional<Users> findUser = userRepository.findByIdentification(signUpDto.getIdentification());
        if(findUser.isPresent()){
            throw new DuplicateIdentificationException("The ID already exists.");
        }if(!matchPassword(signUpDto.getPassword(), signUpDto.getConfirmPassword())){
            throw new PasswordMismatchException("Passwords do not match.");
        }else{
            UserMapper userMapper = new UserMapper();
            try{
                Users user = userMapper.mapToUser(signUpDto);
                encodePassword(user, signUpDto.getPassword());
                createCartAndWishList(user);
                createAdditionalUserInfo(user);
                userRepository.save(user);
            }catch (DateTimeParseException e){
                throw new DateTimeParseException("Invalid birth date.", signUpDto.getBirthDate(),
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
     * @throws DuplicateIdentificationException Thrown if the given identification already exists
     */
    @Override
    public void checkDuplicateIdentification(String identification) {
        if(isIdentificationExist(identification)){
            throw new DuplicateIdentificationException("The ID already exists.");
        }
    }

    /**
     * @param user
     * Generate additional user information
     */
    private void createAdditionalUserInfo(Users user) {
        user.updatePoint(0);
        user.updateTier(IRON);
        user.updateAmountToNextTier(50000);
        user.updateRole(Roles.ROLE_USER);
    }

    /**
     * @param user
     * Create a shopping cart and wishlist simultaneously upon user creation
     */
    private void createCartAndWishList(Users user) {
        Cart cart = new Cart(0L);
        WishList wishList = new WishList();

        cartRepository.save(cart);
        wishListRepository.save(wishList);
        user.updateCart(cart);
        user.updateWishList(wishList);
    }

    /**
     * @param user
     * @param password
     * Encode a password with PasswordEncoder
     */
    private void encodePassword(Users user, String password) {
        String hashPassword = passwordEncoder.encode(password);
        user.updatePassword(hashPassword);
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

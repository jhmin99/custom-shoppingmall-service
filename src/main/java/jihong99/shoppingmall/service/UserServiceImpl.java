package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.dto.*;
import jihong99.shoppingmall.entity.*;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.mapper.UserMapper;
import jihong99.shoppingmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static jihong99.shoppingmall.constants.Constants.*;
import static jihong99.shoppingmall.entity.enums.Tiers.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishListRepository wishListRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final IDeliveryAddressService deliveryAddressService;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * Registers a new user account.
     *
     * @param signUpDto the data transfer object containing user sign up details
     * @throws DuplicateNameException if the identification is already in use
     * @throws PasswordMismatchException if the password and confirmation password do not match
     * @throws RuntimeException if welcome coupon doesn't exist
     */
    @Override
    @Transactional
    public void signUpAccount(SignUpDto signUpDto) {
        Optional<Users> findUser = userRepository.findByIdentification(signUpDto.getIdentification());
        if (findUser.isPresent()) {
            throw new DuplicateNameException(MESSAGE_400_duplicatedId);
        }
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new PasswordMismatchException(MESSAGE_400_MisMatchPw);
        }
        UserMapper userMapper = new UserMapper();
        Users user = userMapper.mapToUser(signUpDto);
        encodePassword(user, signUpDto.getPassword());
        createCartAndWishList(user);
        createAdditionalUserInfo(user);
        Users savedUser = userRepository.save(user);
        assignWelcomeCoupon(savedUser);
    }

    private void assignWelcomeCoupon(Users user) {
        Coupon welcomeCoupon = couponRepository.findByName("Welcome Coupon")
                .orElseThrow(() -> new RuntimeException(MESSAGE_500_CouponNotFound));
        UserCoupon userCoupon = UserCoupon.createUserCoupon(user, welcomeCoupon);
        userCouponRepository.save(userCoupon);
    }

    /**
     * Checks if the provided identification is already in use.
     *
     * @param identification the identification to check
     * @throws DuplicateNameException if the identification is already in use
     */
    @Override
    public void checkDuplicateIdentification(String identification) {
        if (isIdentificationExist(identification)) {
            throw new DuplicateNameException("The ID already exists.");
        }
    }

    /**
     * Authenticates a user using their identification and password.
     *
     * @param loginRequestDto the data transfer object containing login details
     * @return the authenticated user
     * @throws BadCredentialsException if the authentication fails
     */
    @Override
    @Transactional
    public Users loginByIdentificationAndPassword(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getIdentification(), loginRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();
        Users user = userDetailsDto.getUser();

        String refreshToken = generateRefreshToken(user);
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return user;
    }

    /**
     * Generates an access token for the user.
     *
     * @param user the user for whom to generate the access token
     * @return the generated access token
     */
    @Override
    public String generateAccessToken(Users user) {
        return jwtTokenProvider.generateAccessToken(user);
    }

    /**
     * Generates a refresh token for the user.
     *
     * @param user the user for whom to generate the refresh token
     * @return the generated refresh token
     */
    @Override
    public String generateRefreshToken(Users user) {
        return jwtTokenProvider.generateRefreshToken(user);
    }

    /**
     * Retrieves the user details along with their delivery addresses.
     *
     * @param userId the ID of the user whose details are to be retrieved
     * @return the user details and delivery addresses
     * @throws NotFoundException if the user with the specified ID is not found
     */
    @Override
    public MyPageResponseDto getUserDetails(Long userId) {
        Users findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
        Set<DeliveryAddress> deliveryAddresses = deliveryAddressService.getDeliveryAddresses(findUser);
        return MyPageResponseDto.of(findUser,deliveryAddresses);
    }

    /**
     * Registers a new admin account.
     *
     * @param signUpDto the data transfer object containing admin sign up details
     * @throws DuplicateNameException if the identification is already in use
     * @throws PasswordMismatchException if the password and confirmation password do not match
     */
    @Override
    @Transactional
    public void signUpAdminAccount(SignUpDto signUpDto) {
        Optional<Users> findUser = userRepository.findByIdentification(signUpDto.getIdentification());
        if (findUser.isPresent()) {
            throw new DuplicateNameException(MESSAGE_400_duplicatedId);
        }
        if (!signUpDto.getPassword().equals(signUpDto.getConfirmPassword())) {
            throw new PasswordMismatchException(MESSAGE_400_MisMatchPw);
        }
        UserMapper userMapper = new UserMapper();
        Users user = userMapper.mapToUser(signUpDto);
        encodePassword(user, signUpDto.getPassword());
        user.updateRole(Roles.ADMIN);
        userRepository.save(user);
    }


    /**
     * Creates additional user information such as points, tier, and role.
     *
     * @param user the user for whom to create additional information
     */
    private void createAdditionalUserInfo(Users user) {
        user.updatePoint(0);
        user.updateTier(IRON);
        user.updateAmountToNextTier(50000);
        user.updateRole(Roles.USER);
    }

    /**
     * Creates a shopping cart and wishlist for the user.
     *
     * @param user the user for whom to create the cart and wishlist
     */
    private void createCartAndWishList(Users user) {
        Cart cart = Cart.createCart(0L);
        WishList wishList = WishList.createWishList();
        cartRepository.save(cart);
        wishListRepository.save(wishList);
        user.updateCart(cart);
        user.updateWishList(wishList);
    }

    /**
     * Encodes the user's password.
     *
     * @param user the user whose password to encode
     * @param password the password to encode
     */
    private void encodePassword(Users user, String password) {
        String hashPassword = passwordEncoder.encode(password);
        user.updatePassword(hashPassword);
    }

    /**
     * Checks if the identification exists in the repository.
     *
     * @param identification the identification to check
     * @return true if the identification exists, false otherwise
     */
    private boolean isIdentificationExist(String identification) {
        return userRepository.findByIdentification(identification).isPresent();
    }
    /**
     * Retrieves a paginated list of user summaries.
     *
     * @param pageable the pagination information
     * @return a page of user summaries
     */
    @Override
    public Page<UserSummaryDto> getUsers(Pageable pageable) {
        return userRepository.findAllUserSummaries(pageable);
    }
}

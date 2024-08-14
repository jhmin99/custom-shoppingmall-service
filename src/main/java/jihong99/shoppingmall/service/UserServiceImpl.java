package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.config.auth.UserDetailsDto;
import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.dto.request.LoginRequestDto;
import jihong99.shoppingmall.dto.request.PatchAdminRequestDto;
import jihong99.shoppingmall.dto.request.PatchUserRequestDto;
import jihong99.shoppingmall.dto.request.SignUpRequestDto;
import jihong99.shoppingmall.dto.response.AdminSummaryResponseDto;
import jihong99.shoppingmall.dto.response.UserDetailsResponseDto;
import jihong99.shoppingmall.dto.response.UserSummaryResponseDto;
import jihong99.shoppingmall.entity.*;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.exception.NotFoundException;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static jihong99.shoppingmall.constants.Constants.*;

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

    /**
     * Registers a new user account.
     *
     * @param signUpRequestDto the data transfer object containing user sign up details
     * @throws DuplicateNameException if the identification is already in use
     * @throws PasswordMismatchException if the password and confirmation password do not match
     */
    @Override
    @Transactional
    public void signUpAccount(SignUpRequestDto signUpRequestDto) {
        Optional<Users> findUser = userRepository.findByIdentification(signUpRequestDto.getIdentification());
        if (findUser.isPresent()) {
            throw new DuplicateNameException(MESSAGE_400_duplicatedId);
        }
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getConfirmPassword())) {
            throw new PasswordMismatchException(MESSAGE_400_MisMatchPw);
        }
        Users user = getUsers(signUpRequestDto);
        encodePassword(user, signUpRequestDto.getPassword());
        createCartAndWishList(user);
        userRepository.save(user);
    }

    /**
     * Helper method to map SignUpRequestDto to a Users entity.
     *
     * @param signUpRequestDto the data transfer object containing user sign up details
     * @return a Users entity populated with data from the SignUpRequestDto
     */
    private static Users getUsers(SignUpRequestDto signUpRequestDto) {
        return Users.createUsers(signUpRequestDto.getIdentification(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getName(),
                LocalDate.parse(signUpRequestDto.getBirthDate()),
                signUpRequestDto.getPhoneNumber()
        );
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
     * Registers a new admin account.
     *
     * @param signUpRequestDto the data transfer object containing admin sign up details
     * @throws DuplicateNameException if the identification is already in use
     * @throws PasswordMismatchException if the password and confirmation password do not match
     */
    @Override
    @Transactional
    public void signUpAdminAccount(SignUpRequestDto signUpRequestDto) {
        Optional<Users> findUser = userRepository.findByIdentification(signUpRequestDto.getIdentification());
        if (findUser.isPresent()) {
            throw new DuplicateNameException(MESSAGE_400_duplicatedId);
        }
        if (!signUpRequestDto.getPassword().equals(signUpRequestDto.getConfirmPassword())) {
            throw new PasswordMismatchException(MESSAGE_400_MisMatchPw);
        }
        Users user = getAdmin(signUpRequestDto);
        encodePassword(user, signUpRequestDto.getPassword());
        userRepository.save(user);
    }

    /**
     * Helper method to map SignUpRequestDto to an Admin Users entity.
     *
     * @param signUpRequestDto the data transfer object containing admin sign up details
     * @return a Users entity populated with admin data from the SignUpRequestDto
     */
    private static Users getAdmin(SignUpRequestDto signUpRequestDto) {
        return Users.createAdmin(
                signUpRequestDto.getIdentification(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getName(),
                LocalDate.parse(signUpRequestDto.getBirthDate()),
                signUpRequestDto.getPhoneNumber());
    }


    /**
     * Creates a shopping cart and wishlist for the user.
     *
     * @param user the user for whom to create the cart and wishlist
     */
    private void createCartAndWishList(Users user) {
        Cart cart = Cart.createCart();
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
    public Page<UserSummaryResponseDto> getUsers(Pageable pageable) {
        Page<Users> users = userRepository.findAllByRole(Roles.USER, pageable);
        return getUserSummaryResponseDtos(users);

    }

    /**
     * Helper method to map Users entities to UserSummaryResponseDto.
     *
     * @param users the page of Users entities
     * @return a page of UserSummaryResponseDto
     */
    private static Page<UserSummaryResponseDto> getUserSummaryResponseDtos(Page<Users> users) {
        return users.map(user -> UserSummaryResponseDto.of(
                user.getId(),
                user.getIdentification(),
                user.getName(),
                user.getBirthDate(),
                user.getPhoneNumber(),
                user.getCreationTime(),
                user.getLastModifiedTime()
        ));
    }

    /**
     * Retrieves a paginated list of admin summaries.
     *
     * @param pageable the pagination information
     * @return a page of admin summaries
     */
    @Override
    public Page<AdminSummaryResponseDto> getAdmins(Pageable pageable) {
        Page<Users> users = userRepository.findAllByRole(Roles.ADMIN, pageable);
        return getAdminSummaryResponseDtos(users);

    }

    /**
     * Helper method to map Users entities to AdminSummaryResponseDto.
     *
     * @param users the page of Users entities
     * @return a page of AdminSummaryResponseDto
     */
    private static Page<AdminSummaryResponseDto> getAdminSummaryResponseDtos(Page<Users> users) {
        return users.map(user -> AdminSummaryResponseDto.of(
                user.getId(),
                user.getIdentification(),
                user.getName(),
                user.getBirthDate(),
                user.getPhoneNumber(),
                user.getCreationTime(),
                user.getLastModifiedTime()
        ));
    }

    /**
     * Updates the details of an admin account.
     *
     * @param userId the ID of the admin to update
     * @param patchAdminRequestDto the data transfer object containing updated admin details
     * @throws NotFoundException if the admin with the specified ID is not found
     */
    @Override
    @Transactional
    public void patchAdminAccount(Long userId, PatchAdminRequestDto patchAdminRequestDto) {
        Users user = validateUsersExist(userId);
        if (patchAdminRequestDto.getName() != null && !patchAdminRequestDto.getName().isEmpty()) {
            user.updateName(patchAdminRequestDto.getName());
        }

        if (patchAdminRequestDto.getPassword() != null && !patchAdminRequestDto.getPassword().isEmpty()) {
            user.updatePassword(patchAdminRequestDto.getPassword());
        }

        if (patchAdminRequestDto.getPhoneNumber() != null && !patchAdminRequestDto.getPhoneNumber().isEmpty()) {
            user.updatePhoneNumber(patchAdminRequestDto.getPhoneNumber());
        }

        if (patchAdminRequestDto.getRole() != null) {
            user.updateRole(patchAdminRequestDto.getRole());
        }
    }

    /**
     * Deletes an admin account.
     *
     * @param userId the ID of the admin to delete
     * @throws NotFoundException if the admin with the specified ID is not found
     */
    @Override
    @Transactional
    public void deleteAdminAccount(Long userId) {
        Users user = validateUsersExist(userId);
        userRepository.delete(user);
    }

    /**
     * Updates the details of a user account.
     *
     * @param userId the ID of the user to update
     * @param patchUserRequestDto the data transfer object containing updated user details
     * @throws NotFoundException if the user with the specified ID is not found
     */
    @Override
    @Transactional
    public void patchUserAccount(Long userId, PatchUserRequestDto patchUserRequestDto) {
        Users user = validateUsersExist(userId);
        if (patchUserRequestDto.getName() != null && !patchUserRequestDto.getName().isEmpty()) {
            user.updateName(patchUserRequestDto.getName());
        }

        if (patchUserRequestDto.getBirthDate() != null && !patchUserRequestDto.getBirthDate().isEmpty()) {
            user.updatePassword(patchUserRequestDto.getBirthDate());
        }

        if (patchUserRequestDto.getPhoneNumber() != null && !patchUserRequestDto.getPhoneNumber().isEmpty()) {
            user.updatePhoneNumber(patchUserRequestDto.getPhoneNumber());
        }

    }

    /**
     * Retrieves the user details along with their delivery addresses.
     *
     * @param userId the ID of the user whose details are to be retrieved
     * @return the user details and delivery addresses
     * @throws NotFoundException if the user with the specified ID is not found
     */
    @Override
    public UserDetailsResponseDto getUserDetails(Long userId) {
        Users findUser = validateUsersExist(userId);
        Set<DeliveryAddress> deliveryAddresses = deliveryAddressService.getDeliveryAddresses(findUser);
        return UserDetailsResponseDto.of(findUser, deliveryAddresses);
    }

    /**
     * Validates that a user exists by ID.
     *
     * @param userId the ID of the user to validate
     * @return the Users entity if found
     * @throws NotFoundException if the user with the specified ID is not found
     */
    private Users validateUsersExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }
}

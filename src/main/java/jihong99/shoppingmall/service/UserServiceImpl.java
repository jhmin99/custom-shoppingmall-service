package jihong99.shoppingmall.service;

import jihong99.shoppingmall.config.auth.UserDetailsDto;
import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.dto.request.auth.LoginRequestDto;
import jihong99.shoppingmall.dto.request.user.PatchAdminRequestDto;
import jihong99.shoppingmall.dto.request.user.PatchUserRequestDto;
import jihong99.shoppingmall.dto.request.auth.SignUpRequestDto;
import jihong99.shoppingmall.dto.response.user.AdminSummaryResponseDto;
import jihong99.shoppingmall.dto.response.user.UserDetailsResponseDto;
import jihong99.shoppingmall.dto.response.user.UserSummaryResponseDto;
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
import org.springframework.transaction.annotation.Transactional;

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
        Users user = createUserFromSignUpRequest(signUpRequestDto);
        hashAndSetUserPassword(user, signUpRequestDto.getPassword());
        initializeCartAndWishListForUser(user);
        userRepository.save(user);
    }


    /**
     * Checks if the provided identification is already in use.
     *
     * @param identification the identification to check
     * @throws DuplicateNameException if the identification is already in use
     */
    @Override
    public void checkDuplicateIdentification(String identification) {
        if (doesIdentificationExist(identification)) {
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
        Users user = createAdminFromSignUpRequest(signUpRequestDto);
        hashAndSetUserPassword(user, signUpRequestDto.getPassword());
        userRepository.save(user);
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
        return mapUsersToUserSummaryResponseDtos(users);

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
        return mapUsersToAdminSummaryResponseDtos(users);

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
        Users user = findUserOrThrow(userId);
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
        Users user = findUserOrThrow(userId);
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
        Users user = findUserOrThrow(userId);
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
        Users findUser = findUserOrThrow(userId);
        Set<DeliveryAddress> deliveryAddresses = deliveryAddressService.findDeliveryAddressesByUser(findUser);
        return UserDetailsResponseDto.of(findUser, deliveryAddresses);
    }



    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }

    private static Page<AdminSummaryResponseDto> mapUsersToAdminSummaryResponseDtos(Page<Users> users) {
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

    private static Users createUserFromSignUpRequest(SignUpRequestDto signUpRequestDto) {
        return Users.createUsers(signUpRequestDto.getIdentification(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getName(),
                LocalDate.parse(signUpRequestDto.getBirthDate()),
                signUpRequestDto.getPhoneNumber()
        );
    }
    private static Page<UserSummaryResponseDto> mapUsersToUserSummaryResponseDtos(Page<Users> users) {
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
    private static Users createAdminFromSignUpRequest(SignUpRequestDto signUpRequestDto) {
        return Users.createAdmin(
                signUpRequestDto.getIdentification(),
                signUpRequestDto.getPassword(),
                signUpRequestDto.getName(),
                LocalDate.parse(signUpRequestDto.getBirthDate()),
                signUpRequestDto.getPhoneNumber());
    }
    private void initializeCartAndWishListForUser(Users user) {
        Cart cart = Cart.createCart();
        WishList wishList = WishList.createWishList();
        cartRepository.save(cart);
        wishListRepository.save(wishList);
        user.updateCart(cart);
        user.updateWishList(wishList);
    }
    private void hashAndSetUserPassword(Users user, String password) {
        String hashPassword = passwordEncoder.encode(password);
        user.updatePassword(hashPassword);
    }

    private boolean doesIdentificationExist(String identification) {
        return userRepository.findByIdentification(identification).isPresent();
    }
}

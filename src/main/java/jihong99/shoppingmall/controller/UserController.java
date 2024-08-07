package jihong99.shoppingmall.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jihong99.shoppingmall.dto.request.LoginRequestDto;
import jihong99.shoppingmall.dto.request.PatchUserRequestDto;
import jihong99.shoppingmall.dto.request.SignUpRequestDto;
import jihong99.shoppingmall.dto.response.*;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.utils.annotation.HasId;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

import static jihong99.shoppingmall.constants.Constants.*;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final IUserService iuserService;

    /**
     * User Registration
     *
     * <p>Registers a new user via a POST request.</p>
     *
     * @param signUpRequestDto DTO object containing necessary information for user registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the user registration
     * @success User object successfully created
     * Response Code: 201
     * @throws MethodArgumentNotValidException Validation failed (groups: {SignUpValidation.class})
     * Response Code: 400
     * @throws DuplicateNameException Duplicate ID exists
     * Response Code: 400
     * @throws PasswordMismatchException Password mismatch
     * Response Code: 400
     * @throws DateTimeParseException Unable to convert the birthdate string to LocalDate
     * Response Code: 400
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody @Validated(SignUpValidation.class) SignUpRequestDto signUpRequestDto) {
        iuserService.signUpAccount(signUpRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createUser));

    }


    /**
     * Checks if the provided ID is already in use.
     *
     * <p>This endpoint is used to check if the provided ID is already in use during user registration.
     * It validates only the identification field of the SignUpRequestDto using the IdentificationValidation group.</p>
     *
     * @param signUpRequestDto DTO object containing necessary information for user registration.
     *                         Only the identification field is validated in this method.
     * @return ResponseEntity containing the result of the ID duplication check.
     * @throws MethodArgumentNotValidException if the validation of the identification field fails.
     *                                         Response Code: 400.
     * @throws DuplicateNameException if the ID already exists. Response Code: 400.
     * @throws Exception if an internal server error occurs. Response Code: 500.
     */
    @PostMapping("/check-id")
    public ResponseEntity<ResponseDto> verifyIdentification(@RequestBody @Validated(IdentificationValidation.class) SignUpRequestDto signUpRequestDto) {
        iuserService.checkDuplicateIdentification(signUpRequestDto.getIdentification());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_verifiedId));

    }



    /**
     * Authenticates a user by their identification and password.
     *
     * <p>This endpoint processes the login of a user by verifying their credentials.
     * If the authentication is successful, a success message is returned.
     * Otherwise, an error message is returned.</p>
     *
     * @param loginRequestDto DTO object containing the user's login information
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @return ResponseEntity<LoginResponseDto> Response object containing the result of the login attempt
     * @success Valid response indicating that the login was successful
     * Response Code: 200
     * @throws BadCredentialsException Thrown if the credentials provided are invalid
     * Response Code: 400
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response){
        Users user = iuserService.loginByIdentificationAndPassword(loginRequestDto);
        String accessToken = iuserService.generateAccessToken(user);
        String refreshToken = iuserService.generateRefreshToken(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LoginResponseDto.of(accessToken, refreshToken, user.getId()));
    }
    /**
     * Processes logout by invalidating the current user's authentication information.
     *
     * @return ResponseEntity with a logout message.
     * @throws AccessDeniedException if the user is not authorized.
     *                               Response Code: 403.
     * @throws Exception if an internal server error occurs.
     *                   Response Code: 500.
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info("User logged out: {}", authentication.getName());
        SecurityContextHolder.clearContext();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_LogoutSuccess));
    }



    /**
     * Retrieves a summary list of users.
     *
     * <p>This endpoint is accessible only by users with the 'ADMIN' role.
     * It retrieves a paginated summary list of all users.</p>
     *
     * @param page the page number to retrieve (for pagination)
     * @param size the number of users to retrieve per page (maximum 10)
     * @return ResponseEntity<PaginatedResponseDto<UserSummaryDto>> a paginated response containing the user summaries
     * @success Valid response containing the paginated user summaries
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws Exception Internal server error occurred
     * Response Code: 500
     *
     * */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<UserSummaryResponseDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSummaryResponseDto> userSummaries = iuserService.getUsers(pageable);
        PaginatedResponseDto<UserSummaryResponseDto> response = PaginatedResponseDto.of(userSummaries);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

    /**
     * Retrieves detailed information about a user for admin purposes.
     *
     * <p>This endpoint is accessible only by users with the 'ADMIN' role.</p>
     *
     * @param userId The ID of the user whose details are being requested
     * @return ResponseEntity<UserDetailsResponseDto> containing the user's details
     * @success Valid response containing the user's details
     * Response Code: 200
     * @throws TypeMismatchException Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have ADMIN role
     * Response Code: 403
     * @throws NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsResponseDto> getUserDetailsForAdmin(@PathVariable Long userId){
        UserDetailsResponseDto userDetails = iuserService.getUserDetails(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetails);
    }

    /**
     * Retrieves user details based on the provided user ID.
     *
     * <p>Checks if the authenticated user has the required ID before retrieving the user details.</p>
     *
     * @param userId The ID of the user whose details are being requested
     * @return ResponseEntity<UserDetailsResponseDto> containing the user's details
     * @success Valid response containing the user's details
     * Response Code: 200
     * @throws TypeMismatchException Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required ID
     * Response Code: 403
     * @throws NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @HasId
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailsResponseDto> getUserDetails(@PathVariable Long userId) {
        UserDetailsResponseDto userDetails = iuserService.getUserDetails(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetails);

    }

    /**
     * Patch user details based on the provided user ID.
     *
     * <p>Checks if the authenticated user has the required ID before updating the user details.</p>
     *
     * @param userId The ID of the user whose details are being updated
     * @param patchUserRequestDto DTO object containing the updated user details
     * @return ResponseEntity<ResponseDto> containing the result of the user update
     * @success User account successfully updated
     * Response Code: 200
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws TypeMismatchException Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required ID
     * Response Code: 403
     * @throws NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @HasId
    @PatchMapping("/users/{userId}")
    public ResponseEntity<ResponseDto> patchUserDetails(@PathVariable Long userId, PatchUserRequestDto patchUserRequestDto){
        iuserService.patchUserAccount(userId, patchUserRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateUserSuccess));
    }

}

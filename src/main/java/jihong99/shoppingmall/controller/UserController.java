package jihong99.shoppingmall.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jihong99.shoppingmall.dto.*;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.utils.annotation.HasId;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
     * Check Duplicate ID
     *
     * <p>This endpoint is used to check if the provided ID is already in use.</p>
     *
     * @param signUpDto DTO object containing necessary information for user registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the ID duplication check
     * @success Valid response indicating that the ID is available for use
     * Response Code: 200
     * @exception MethodArgumentNotValidException Validation failed (Validating only the identification field of SignUpDto, groups: {IdentificationValidation.class})
     * Response Code: 400
     * @exception DuplicateIdentificationException Duplicate ID exists
     * Response Code: 400
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/users/check-id")
    public ResponseEntity<ResponseDto> verifyIdentification(@RequestBody @Validated(IdentificationValidation.class) SignUpDto signUpDto) {
        iuserService.checkDuplicateIdentification(signUpDto.getIdentification());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_verifiedId));

    }

    /**
     * User Registration
     *
     * <p>Registers a new user via a POST request.</p>
     *
     * @param signUpDto DTO object containing necessary information for user registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the user registration
     * @success User object successfully created
     * Response Code: 201
     * @exception MethodArgumentNotValidException Validation failed (groups: {SignUpValidation.class})
     * Response Code: 400
     * @exception DuplicateIdentificationException Duplicate ID exists
     * Response Code: 400
     * @exception PasswordMismatchException Password mismatch
     * Response Code: 400
     * @exception DateTimeParseException Unable to convert the birthdate string to LocalDate
     * Response Code: 400
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody @Validated(SignUpValidation.class) SignUpDto signUpDto) {
        iuserService.signUpAccount(signUpDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createUser));

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
     * @exception BadCredentialsException Thrown if the credentials provided are invalid
     * Response Code: 400
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response){
        Users user = iuserService.loginByIdentificationAndPassword(loginRequestDto);
        String accessToken = iuserService.generateAccessToken(user);
        String refreshToken = iuserService.generateRefreshToken(user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LoginResponseDto.success(accessToken, refreshToken, user.getId()));
    }
    /**
     * Logout processing
     *
     * <p>Processes logout by invalidating the current user's authentication information.</p>
     *
     * @return ResponseEntity with a logout message
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
     * Retrieves user details based on the provided user ID.
     *
     * <p>Checks if the authenticated user has the required ID before retrieving the user details.</p>
     *
     * @param userId The ID of the user whose details are being requested
     * @return ResponseEntity<MyPageResponseDto> containing the user's details
     * @success Valid response containing the user's details
     * Response Code: 200
     * @exception NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the specified userId
     */
    @HasId
    @GetMapping("/users")
    public ResponseEntity<MyPageResponseDto> getUserDetails(@RequestParam Long userId) {
        MyPageResponseDto userDetails = iuserService.getUserDetails(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDetails);

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
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<UserSummaryDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserSummaryDto> userSummaries = iuserService.getUsers(pageable);
        PaginatedResponseDto<UserSummaryDto> response = PaginatedResponseDto.of(userSummaries, STATUS_200, MESSAGE_200_fetchSuccess);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);

    }

}

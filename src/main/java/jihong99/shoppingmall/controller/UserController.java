package jihong99.shoppingmall.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.*;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

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
        try {
            iuserService.checkDuplicateIdentification(signUpDto.getIdentification());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(UserConstants.STATUS_200, UserConstants.MESSAGE_200_verifiedId));
        } catch (DuplicateIdentificationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_duplicatedId));
        }
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
        try {
            iuserService.signUpAccount(signUpDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(UserConstants.STATUS_201, UserConstants.MESSAGE_201_createUser));
        } catch (DuplicateIdentificationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_duplicatedId));
        } catch (PasswordMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_MissMatchPw));
        } catch (DateTimeParseException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_WrongBirthDate));
        }
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
        try{
            Users user = iuserService.loginByIdentificationAndPassword(loginRequestDto);
            String accessToken = iuserService.generateAccessToken(user);
            String refreshToken = iuserService.generateRefreshToken(user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new LoginResponseDto(UserConstants.STATUS_200, "login success", accessToken, refreshToken, user.getId()));
        }catch (BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDto(UserConstants.STATUS_400, "login failed", null, null, null));
        }
    }
    /**
     * Logout processing
     *
     * <p>Processes logout by invalidating the current user's authentication information.</p>
     *
     * @return Logout message
     */
    @PostMapping("/logout")
    public String logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info(authentication.toString());
        if (authentication != null) {
            SecurityContextHolder.clearContext();
            return "You have been logged out.";
        } else {
            return "You are not logged in.";
        }
    }
}

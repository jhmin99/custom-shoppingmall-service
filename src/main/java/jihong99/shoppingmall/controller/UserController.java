package jihong99.shoppingmall.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.*;
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
     *
     * Check Duplicate ID
     * This endpoint is used to check if the provided ID is already in use.
     * @param signUpDto DTO object containing necessary information for user registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the ID duplication check
     * @success
     * Valid response indicating that the ID is available for use
     * Response Code: 200
     * @exception MethodArgumentNotValidException
     * Validation failed (Validating only the identification field of SignUpDto, groups: {IdentificationValidation.class})
     * Response Code: 400
     * @exception DuplicateIdentificationException
     * Duplicate ID exists
     * Response Code: 400
     * @exception Exception
     * Internal server error occurred
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
     * Registers a new user via a POST request.
     * @param signUpDto DTO object containing necessary information for user registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the user registration
     * @success
     * User object successfully created
     * Response Code: 201
     * @exception MethodArgumentNotValidException
     * Validation failed (groups: {SignUpValidation.class})
     * Response Code: 400
     * @exception DuplicateIdentificationException
     * Duplicate ID exists
     * Response Code: 400
     * @exception PasswordMismatchException
     * Password mismatch
     * Response Code: 400
     * @exception DateTimeParseException
     * Unable to convert the birthdate string to LocalDate
     * Response Code: 400
     * @exception Exception
     * Internal server error occurred
     * Response Code: 500
     **/
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signUp(@RequestBody @Validated(SignUpValidation.class) SignUpDto signUpDto){

        try {
            iuserService.signUpAccount(signUpDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(UserConstants.STATUS_201, UserConstants.MESSAGE_201_createUser));
        }catch (DuplicateIdentificationException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_duplicatedId));
        }catch(PasswordMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_MissMatchPw));
        }catch (DateTimeParseException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, UserConstants.MESSAGE_400_WrongBirthDate));
        }
    }

    //test
    @GetMapping("/users")
    public ResponseEntity<UserDetailsDto> userDetails(Authentication authentication, @RequestParam(name = "id") Long id){
        String authName = authentication.getName();
        UserDetailsDto userDetailsDto = iuserService.getUserDetails(id);
        LOGGER.info(authName);
        LOGGER.info(userDetailsDto.getIdentification());
        if(authName.equals(userDetailsDto.getIdentification())){
            return ResponseEntity.ok()
                    .body(userDetailsDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }




    /**
     * Authenticates a user by their identification and password.
     *
     * <p>This endpoint processes the login of a user by verifying their credentials.
     * If the authentication is successful, a success message is returned.
     * Otherwise, an error message is returned.</p>
     *
     * @param loginDto DTO object containing the user's login information
     * @param request HttpServletRequest object
     * @param response HttpServletResponse object
     * @return ResponseEntity<ResponseDto> Response object containing the result of the login attempt
     * @success
     * Valid response indicating that the login was successful
     * Response Code: 200
     * @exception BadCredentialsException
     * Thrown if the credentials provided are invalid
     * Response Code: 400
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response){
        try{
            iuserService.loginByIdentificationAndPassword(loginDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(UserConstants.STATUS_200, "login success"));
        }catch (BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(UserConstants.STATUS_400, "login failed"));
        }
    }
    @PostMapping("/logout")
    public String logout(){
        // 현재 사용자의 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOGGER.info(authentication.toString());
        // 인증 정보가 있는 경우 로그아웃 처리
        if (authentication != null) {
            // 현재 세션을 무효화하여 로그아웃 처리
            SecurityContextHolder.clearContext();
            return "로그아웃되었습니다.";
        } else {
            // 인증 정보가 없는 경우 로그인되지 않았으므로 에러 메시지 반환
            return "로그인되어 있지 않습니다.";
        }
    }

}

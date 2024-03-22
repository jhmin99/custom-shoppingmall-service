package jihong99.shoppingmall.controller;
import jihong99.shoppingmall.constants.UserConstants;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.dto.UserDetailsDto;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

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
    @PostMapping("/users")
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
    public ResponseEntity<UserDetailsDto> userDetails(@RequestParam Long id){
        UserDetailsDto userDetailsDto = iuserService.getUserDetails(id);
        return ResponseEntity.ok()
                .body(userDetailsDto);
    }
}

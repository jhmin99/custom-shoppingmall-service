package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeParseException;
import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final IUserService iuserService;
    /**
     * Create a new admin account.
     *
     * <p>This endpoint allows a SUPER_ADMIN to create a new admin account.
     * The request body must contain valid admin account details.</p>
     *
     * @param adminRequest DTO object containing necessary information for admin registration
     * @return ResponseEntity<ResponseDto> Response object containing the result of the admin registration
     * @success Admin account successfully created
     * Response Code: 201
     * @exception MethodArgumentNotValidException Validation failed (groups: {SignUpValidation.class})
     * Response Code: 400
     * @exception DuplicateNameException Thrown if the identification already exists
     * Response Code: 400
     * @exception PasswordMismatchException Thrown if the passwords do not match
     * Response Code: 400
     * @exception DateTimeParseException Thrown if the birthdate is in an invalid format
     * Response Code: 400
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/create-admin")
    public ResponseEntity<ResponseDto> createAdmin(@RequestBody @Validated(SignUpValidation.class) SignUpDto adminRequest) {
        iuserService.signUpAdminAccount(adminRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createUser));
    }
}


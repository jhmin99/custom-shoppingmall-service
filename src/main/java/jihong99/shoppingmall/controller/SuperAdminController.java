package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeParseException;

import static jihong99.shoppingmall.constants.Constants.*;
import static jihong99.shoppingmall.constants.Constants.MESSAGE_400_WrongBirthDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final IUserService iuserService;

    @PostMapping("/create-admin")
    public ResponseEntity<ResponseDto> createAdmin(@RequestBody @Validated(SignUpValidation.class) SignUpDto adminRequest) {
        try {
            iuserService.signUpAdminAccount(adminRequest);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseDto(STATUS_201, MESSAGE_201_createUser));
        } catch (DuplicateIdentificationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(STATUS_400, MESSAGE_400_duplicatedId));
        } catch (PasswordMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(STATUS_400, MESSAGE_400_MissMatchPw));
        } catch (DateTimeParseException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto(STATUS_400, MESSAGE_400_WrongBirthDate));
        }
    }
}


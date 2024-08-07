package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.request.PatchAdminRequestDto;
import jihong99.shoppingmall.dto.response.AdminSummaryResponseDto;
import jihong99.shoppingmall.dto.response.PaginatedResponseDto;
import jihong99.shoppingmall.dto.response.ResponseDto;
import jihong99.shoppingmall.dto.request.SignUpRequestDto;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.service.IUserService;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/super-admin", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @throws MethodArgumentNotValidException Validation failed (groups: {SignUpValidation.class})
     *                                         Response Code: 400
     * @throws DuplicateNameException          Thrown if the identification already exists
     *                                         Response Code: 400
     * @throws PasswordMismatchException       Thrown if the passwords do not match
     *                                         Response Code: 400
     * @throws DateTimeParseException          Thrown if the birthdate is in an invalid format
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have SUPER_ADMIN role
     *                                         Response Code: 403
     * @throws Exception                       Internal server error occurred
     *                                         Response Code: 500
     * @success Admin account successfully created
     * Response Code: 201
     */
    @PostMapping("/admins")
    public ResponseEntity<ResponseDto> createAdmin(@RequestBody @Validated(SignUpValidation.class) SignUpRequestDto adminRequest) {
        iuserService.signUpAdminAccount(adminRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createUser));
    }


    /**
     * Get a paginated list of admin summaries.
     *
     * <p>This endpoint allows a SUPER_ADMIN to retrieve a paginated list of admin summaries.</p>
     *
     * @param page The page number to retrieve (default is 0)
     * @param size The number of records per page (default is 10)
     * @return ResponseEntity<PaginatedResponseDto<AdminSummaryResponseDto>> Response object containing the paginated list of admin summaries
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have SUPER_ADMIN role
     *                                         Response Code: 403
     * @throws Exception                       Internal server error occurred
     *                                         Response Code: 500
     * @success Paginated list of admin summaries successfully retrieved
     * Response Code: 200
     */
    @GetMapping("/admins")
    public ResponseEntity<PaginatedResponseDto<AdminSummaryResponseDto>> getAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminSummaryResponseDto> adminSummaries = iuserService.getAdmins(pageable);
        PaginatedResponseDto<AdminSummaryResponseDto> response = PaginatedResponseDto.of(adminSummaries);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Patch admin account details.
     *
     * <p>This endpoint allows a SUPER_ADMIN to update an admin account's details.</p>
     *
     * @param userId The ID of the admin user to update
     * @param patchAdminRequestDto DTO object containing the updated admin details
     * @return ResponseEntity<ResponseDto> Response object containing the result of the admin update
     * @success Admin account successfully updated
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws MethodArgumentNotValidException Validation failed
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have SUPER_ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException               Thrown if the admin user is not found
     *                                         Response Code: 404
     * @throws Exception Internal server error occurred
     *                  Response Code: 500
     */
    @PatchMapping("/admins/{userId}")
    public ResponseEntity<ResponseDto> patchAdmin(@PathVariable Long userId, @RequestBody @Validated PatchAdminRequestDto patchAdminRequestDto) {
        iuserService.patchAdminAccount(userId, patchAdminRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateAdminSuccess));
    }

    /**
     * Delete an admin account.
     *
     * <p>This endpoint allows a SUPER_ADMIN to delete an admin account.</p>
     *
     * @param userId The ID of the admin user to delete
     * @return ResponseEntity<ResponseDto> Response object containing the result of the admin deletion
     * @success Admin account successfully deleted
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have SUPER_ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException Thrown if the admin user is not found
     *                           Response Code: 404
     * @throws Exception Internal server error occurred
     *                  Response Code: 500
     */
    @DeleteMapping("/admins/{userId}")
    public ResponseEntity<ResponseDto> deleteAdmin(@PathVariable Long userId) {
        iuserService.deleteAdminAccount(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteAdminSuccess));
    }
}

package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.inquiry.ChildResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.PatchResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.ResponseRequestDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryDetailsResponseDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryResponseDto;
import jihong99.shoppingmall.dto.response.shared.PaginatedResponseDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IInquiryService;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class InquiryController {

    private final IInquiryService iinquiryService;

    /**
     * Retrieves all inquiries based on type, status, and pagination parameters.
     *
     * @param type   The type of the inquiry (item, customer)
     * @param status The status of the inquiry (resolved, unresolved), optional
     * @param page   The page number to retrieve, starting from 0
     * @param size   The number of items per page
     * @return A ResponseEntity containing a paginated list of inquiries
     *
     * @success Response successfully retrieved
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @GetMapping("/admin/inquiries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<InquiryResponseDto>> getAllInquiries(
            @RequestParam InquiryType type,
            @RequestParam(required = false) InquiryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<InquiryResponseDto> inquiries = iinquiryService.getAllInquiries(type, status, pageable);
        PaginatedResponseDto<InquiryResponseDto> response = PaginatedResponseDto.of(inquiries);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves the details of a specific inquiry by its ID.
     *
     * @param inquiryId The ID of the inquiry
     * @return A ResponseEntity containing the details of the inquiry
     *
     * @success Inquiry details successfully retrieved
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (inquiryId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @GetMapping("/admin/inquiries/{inquiryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InquiryDetailsResponseDto> getInquiryDetails(
            @PathVariable Long inquiryId
    ) {
        InquiryDetailsResponseDto inquiryDetails = iinquiryService.getInquiryDetails(inquiryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(inquiryDetails);
    }

    /**
     * Responds to a specific inquiry.
     *
     * <p>This endpoint allows an admin to respond to an inquiry with a given inquiry ID. The response details
     * are provided in the request body.</p>
     *
     * @param inquiryId           The ID of the inquiry to respond to
     * @param responseRequestDto  DTO containing response details such as content
     * @return A ResponseEntity containing the status of the response creation operation
     *
     * @success Response successfully created
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (inquiryId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the parent inquiry is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping("/admin/inquiries/{inquiryId}/responds")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> respondToInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody ResponseRequestDto responseRequestDto
    ) {
        iinquiryService.respondToInquiry(inquiryId, responseRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createRespondSuccess));
    }

    /**
     * Edits an existing response to an inquiry.
     *
     * <p>This endpoint allows an admin to edit a specific response associated with an inquiry. The inquiry ID and
     * response ID must be provided as path variables, and the updated response details are provided in the request body.</p>
     *
     * @param inquiryId              The ID of the inquiry
     * @param responseId             The ID of the response to be edited
     * @param patchRespondRequestDto DTO containing updated response details
     * @return A ResponseEntity containing the status of the response update operation
     *
     * @success Response successfully updated
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (inquiryId or responseId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the parent inquiry or response is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping("/admin/inquiries/{inquiryId}/responses/{responseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> editInquiryResponse(
            @PathVariable Long inquiryId,
            @PathVariable Long responseId,
            @Valid @RequestBody PatchResponseRequestDto patchRespondRequestDto
    ) {
        iinquiryService.editInquiryResponse(inquiryId, responseId, patchRespondRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateRespondSuccess));
    }

    /**
     * Deletes a response to an inquiry.
     *
     * <p>This endpoint allows an admin to delete a response associated with an inquiry. The inquiry ID and
     * response ID are provided as path variables.</p>
     *
     * @param inquiryId The ID of the inquiry
     * @param responseId The ID of the response to be deleted
     * @return A ResponseEntity containing the status of the deletion operation
     *
     * @success Response successfully deleted
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (inquiryId or responseId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry or response is not found
     * Response Code: 404
     *
     * @throws HasRelatedEntitiesException if the response has related replies and cannot be deleted
     * Response Code: 409
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @DeleteMapping("/admin/inquiries/{inquiryId}/responses/{responseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteInquiryResponse(
            @PathVariable Long inquiryId,
            @PathVariable Long responseId
    ) {
        iinquiryService.deleteInquiryResponse(inquiryId, responseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteRespondSuccess));
    }

    /**
     * Responds to a parent response within an inquiry.
     *
     * <p>This endpoint allows an admin to respond to a parent response within an inquiry. The inquiry ID and
     * response details are provided as path variables.</p>
     *
     * @param inquiryId           The ID of the inquiry
     * @param responseId           The ID of the response
     * @param responseRequestDto  DTO containing response details such as content
     * @return A ResponseEntity containing the status of the response creation operation
     *
     * @success Response successfully created
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (inquiryId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the parent inquiry or response is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping("/admin/inquiries/{inquiryId}/responses/{responseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> respondToParentResponse(
            @PathVariable Long inquiryId,
            @PathVariable Long responseId,
            @RequestBody @Valid ChildResponseRequestDto responseRequestDto
    ) {
        iinquiryService.respondToParentResponse(inquiryId, responseId, responseRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createRespondSuccess));
    }
}

package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.inquiry.ChildResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.InquiryRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.PatchResponseRequestDto;
import jihong99.shoppingmall.dto.request.inquiry.ResponseRequestDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryDetailsResponseDto;
import jihong99.shoppingmall.dto.response.inquiry.InquiryResponseDto;
import jihong99.shoppingmall.dto.response.shared.PaginatedResponseDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IInquiryService;
import jihong99.shoppingmall.utils.annotation.HasId;
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
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
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
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
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
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
     * Response Code: 400
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
    /**
     * Submits an item inquiry.
     *
     * <p>This endpoint allows a user to submit an inquiry regarding an item they are interested in.
     * The inquiry details are provided in the request body.</p>
     *
     * @param userId          The ID of the user submitting the inquiry
     * @param itemId          The ID of the item related to the inquiry
     * @param inquiryRequestDto The DTO containing inquiry details (e.g., title, content)
     * @return A ResponseEntity containing the status of the inquiry submission operation
     *
     * @success Inquiry successfully submitted
     * Response Code: 201
     *
     * @throws TypeMismatchException if the method argument (userId or itemId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws InvalidOperationException if the item is invalid or not eligible for an inquiry
     * Response Code: 400
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the user or item is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping("/users/{userId}/items/{itemId}/inquiries")
    @HasId
    public ResponseEntity<ResponseDto> submitItemInquiry(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody InquiryRequestDto inquiryRequestDto) {
        iinquiryService.submitItemInquiry(userId, itemId, inquiryRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createItemInquirySuccess));
    }

    /**
     * Submits a customer inquiry.
     *
     * <p>This endpoint allows a user to submit a general customer inquiry, not related to any specific item.</p>
     *
     * @param userId          The ID of the user submitting the inquiry
     * @param inquiryRequestDto The DTO containing inquiry details (e.g., title, content)
     * @return A ResponseEntity containing the status of the inquiry submission operation
     *
     * @success Inquiry successfully submitted
     * Response Code: 201
     *
     * @throws TypeMismatchException if the method argument (userId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws InvalidOperationException if the inquiry is invalid
     * Response Code: 400
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the user is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping("/users/{userId}/customer/inquiries")
    @HasId
    public ResponseEntity<ResponseDto> submitCustomerInquiry(
            @PathVariable Long userId,
            @Valid @RequestBody InquiryRequestDto inquiryRequestDto) {
        iinquiryService.submitCustomerInquiry(userId, inquiryRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createCustomerInquirySuccess));
    }

    /**
     * Retrieves all inquiries submitted by a user with pagination.
     *
     * <p>This endpoint allows a user to retrieve all of their inquiries with pagination.</p>
     *
     * @param userId The ID of the user
     * @param page   The page number to retrieve, starting from 0 (default 0)
     * @param size   The number of items per page (default 10)
     * @return A ResponseEntity containing a paginated list of inquiries
     *
     * @success Inquiries successfully retrieved
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (userId, page, or size) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the user is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @GetMapping("/users/{userId}/inquiries")
    @HasId
    public ResponseEntity<PaginatedResponseDto<InquiryResponseDto>> getAllUserInquiries(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InquiryResponseDto> inquiries = iinquiryService.getAllUserInquiries(userId, pageable);
        PaginatedResponseDto<InquiryResponseDto> response = PaginatedResponseDto.of(inquiries);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves the details of a specific inquiry submitted by a user.
     *
     * @param userId    The ID of the user
     * @param inquiryId The ID of the inquiry
     * @return A ResponseEntity containing the details of the inquiry
     *
     * @success Inquiry details successfully retrieved
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (userId or inquiryId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry or user is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @GetMapping("/users/{userId}/inquiries/{inquiryId}")
    @HasId
    public ResponseEntity<InquiryDetailsResponseDto> getUserInquiryDetails(
            @PathVariable Long userId,
            @PathVariable Long inquiryId) {
        InquiryDetailsResponseDto inquiryDetails = iinquiryService.getUserInquiryDetails(userId, inquiryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(inquiryDetails);
    }

    /**
     * Deletes a specific inquiry submitted by a user.
     *
     * @param userId    The ID of the user
     * @param inquiryId The ID of the inquiry to be deleted
     * @return A ResponseEntity containing the status of the deletion operation
     *
     * @success Inquiry successfully deleted
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (userId or inquiryId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry or user is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @DeleteMapping("/users/{userId}/inquiries/{inquiryId}")
    @HasId
    public ResponseEntity<ResponseDto> deleteUserInquiry(
            @PathVariable Long userId,
            @PathVariable Long inquiryId) {
        iinquiryService.deleteInquiry(userId, inquiryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_RemoveInquirySuccess));
    }

    /**
     * Responds to a parent response for a specific inquiry submitted by a user.
     *
     * @param userId    The ID of the user
     * @param inquiryId The ID of the inquiry
     * @param responseId The ID of the parent response
     * @param responseRequestDto DTO containing response details such as content
     * @return A ResponseEntity containing the status of the response creation operation
     *
     * @success Response successfully created
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
     * Response Code: 400
     * @throws TypeMismatchException if the method argument (userId, inquiryId, or responseId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the parent inquiry or response is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping("/users/{userId}/inquiries/{inquiryId}/responses/{responseId}")
    @HasId
    public ResponseEntity<ResponseDto> respondToParentResponseForUser(
            @PathVariable Long userId,
            @PathVariable Long inquiryId,
            @PathVariable Long responseId,
            @RequestBody @Valid ChildResponseRequestDto responseRequestDto) {
        iinquiryService.respondToParentResponseForUser(userId, inquiryId, responseId, responseRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createRespondSuccess));
    }

    /**
     * Edits a specific response associated with an inquiry submitted by a user.
     *
     * @param userId    The ID of the user
     * @param inquiryId The ID of the inquiry
     * @param responseId The ID of the response to be edited
     * @param patchResponseRequestDto DTO containing updated response details
     * @return A ResponseEntity containing the status of the response update operation
     *
     * @success Response successfully updated
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
     * Response Code: 400
     * @throws TypeMismatchException if the method argument (userId, inquiryId, or responseId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry or response is not found
     * Response Code: 404
     *
     * @throws HasRelatedEntitiesException if the response contains related replies and cannot be edited
     * Response Code: 409
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping("/users/{userId}/inquiries/{inquiryId}/responses/{responseId}")
    @HasId
    public ResponseEntity<ResponseDto> editInquiryResponse(
            @PathVariable Long userId,
            @PathVariable Long inquiryId,
            @PathVariable Long responseId,
            @Valid @RequestBody PatchResponseRequestDto patchResponseRequestDto) {
        iinquiryService.editInquiryResponseForUser(userId, inquiryId, responseId, patchResponseRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateRespondSuccess));
    }

    /**
     * Deletes a specific response associated with an inquiry submitted by a user.
     *
     * @param userId    The ID of the user
     * @param inquiryId The ID of the inquiry
     * @param responseId The ID of the response to be deleted
     * @return A ResponseEntity containing the status of the deletion operation
     *
     * @success Response successfully deleted
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (userId, inquiryId, or responseId) cannot be converted to the expected type
     * Response Code: 400
     * @throws InvalidOperationException - Out of stock or items are invalid for some reasons
     * Response Code: 400
     * @throws AccessDeniedException if the user does not have the necessary role
     * Response Code: 403
     *
     * @throws NotFoundException if the inquiry or response is not found
     * Response Code: 404
     *
     * @throws HasRelatedEntitiesException if the response contains related replies and cannot be deleted
     * Response Code: 409
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @DeleteMapping("/users/{userId}/inquiries/{inquiryId}/responses/{responseId}")
    @HasId
    public ResponseEntity<ResponseDto> deleteInquiryResponse(
            @PathVariable Long userId,
            @PathVariable Long inquiryId,
            @PathVariable Long responseId) {
        iinquiryService.deleteInquiryResponseForUser(userId, inquiryId, responseId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteRespondSuccess));
    }
}

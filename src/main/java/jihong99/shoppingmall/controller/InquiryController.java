package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.PatchRespondRequestDto;
import jihong99.shoppingmall.dto.request.RespondRequestDto;
import jihong99.shoppingmall.dto.response.ResponseDto;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IInquiryService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
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
     * Responds to an inquiry.
     *
     * <p>This endpoint allows an admin to respond to a specific inquiry. The inquiry ID must be provided
     * as a path variable, and the response details are provided in the request body.</p>
     *
     * @param inquiryId The ID of the inquiry to respond to
     * @param respondRequestDto DTO containing response details such as content
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
    @PostMapping("/admin/inquiries/{inquiryId}/respond")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> respondToInquiry(
            @PathVariable Long inquiryId,
            @Valid @RequestBody RespondRequestDto respondRequestDto
    ) {
        iinquiryService.respondToInquiry(inquiryId, respondRequestDto);
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
     * @param inquiryId The ID of the inquiry
     * @param respondId The ID of the response to be edited
     * @param patchRepondRequestDto DTO containing updated response details
     * @return A ResponseEntity containing the status of the response update operation
     *
     * @success Response successfully updated
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (inquiryId or respondId) cannot be converted to the expected type
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
    @PatchMapping("/admin/inquiries/{inquiryId}/respond/{respondId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> editInquiryResponse(
            @PathVariable Long inquiryId,
            @PathVariable Long respondId,
            @Valid @RequestBody PatchRespondRequestDto patchRepondRequestDto
    ) {
        iinquiryService.editInquiryResponse(inquiryId, respondId, patchRepondRequestDto);
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
     * @param respondId The ID of the response to be deleted
     * @return A ResponseEntity containing the status of the deletion operation
     *
     * @success Response successfully deleted
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (inquiryId or respondId) cannot be converted to the expected type
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
    @DeleteMapping("/admin/inquiries/{inquiryId}/respond/{respondId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteInquiryResponse(
            @PathVariable Long inquiryId,
            @PathVariable Long respondId
    ) {
        iinquiryService.deleteInquiryResponse(inquiryId, respondId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteRespondSuccess));
    }

    /**
     * Updates the status of an inquiry to resolved or unresolved.
     *
     * <p>This endpoint allows an admin to update the status of a specific inquiry. The inquiry ID must be provided
     * as a path variable.</p>
     *
     * @param inquiryId The ID of the inquiry
     * @return A ResponseEntity containing the status of the inquiry update operation
     *
     * @success Inquiry status successfully updated
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
    @PatchMapping("/admin/inquiries/{inquiryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> updateInquiryStatus(
            @PathVariable Long inquiryId
    ) {
        iinquiryService.updateInquiryStatus(inquiryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateInquiryStatusSuccess));
    }

}

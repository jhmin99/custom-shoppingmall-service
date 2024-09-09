package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.notice.NoticeRequestDto;
import jihong99.shoppingmall.dto.request.notice.PatchNoticeRequestDto;
import jihong99.shoppingmall.dto.response.notice.NoticeDetailsResponseDto;
import jihong99.shoppingmall.dto.response.notice.NoticeResponseDto;
import jihong99.shoppingmall.dto.response.shared.PaginatedResponseDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.INoticeService;
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
public class NoticeController {
    private final INoticeService inoticeService;

    /**
     * Creates a new notice.
     *
     * <p>This endpoint allows an admin to create a new notice. The request body must contain valid notice details.</p>
     *
     * @param noticeRequestDto DTO object containing necessary information for notice creation
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notice creation
     * @success Notice successfully created
     * Response Code: 201
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/notices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createNotice(@RequestBody @Valid NoticeRequestDto noticeRequestDto) {
        inoticeService.createNotice(noticeRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createNotice));
    }

    /**
     * Assigns a notice to a specific user.
     *
     * <p>This endpoint allows an admin to assign a specific notice to a specific user.
     * The notice ID and user ID must be specified in the path.</p>
     *
     * @param noticeId the ID of the notice to be assigned
     * @param userId the ID of the user to whom the notice will be assigned
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notice assignment
     * @success Notice successfully assigned to user
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException Notice or user not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/notices/{noticeId}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> assignNoticeToUser(
            @PathVariable Long noticeId,
            @PathVariable Long userId
    ) {
        inoticeService.postNoticeToUser(noticeId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeNoticeSuccess));
    }
    /**
     * Assigns a notice to a specific user.
     *
     * <p>This endpoint allows an admin to assign a specific notice to a specific user.
     * The notice ID and user ID must be specified in the path.</p>
     *
     * @param noticeId the ID of the notice to be assigned
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notice assignment
     * @success Notice successfully assigned to user
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException Notice not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/notices/{noticeId}/assign/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> assignNoticeToAll(
            @PathVariable Long noticeId
    ) {
        inoticeService.postNoticeToAllUsers(noticeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeNoticeSuccess));
    }



    /**
     * Updates an existing notice.
     *
     * <p>This endpoint allows an admin to update an existing notice. The notice ID must be specified in the path,
     * and the request body must contain valid notice details.</p>
     *
     * @param noticeId the ID of the notice to be updated
     * @param patchNoticeRequestDto DTO object containing necessary information for notice update
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notice update
     * @success Notice successfully updated
     * Response Code: 200
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException The notice is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PatchMapping("/admin/notices/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> patchNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid PatchNoticeRequestDto patchNoticeRequestDto
    ) {
        inoticeService.patchNotice(noticeId, patchNoticeRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateNoticeSuccess));
    }

    /**
     * Deletes a notice.
     *
     * <p>This endpoint allows an admin to delete an existing notice. The notice ID must be specified in the path.</p>
     *
     * @param noticeId the ID of the notice to be deleted
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notice deletion
     * @success Notice successfully deleted
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException The notice is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/admin/notices/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteNotice(
            @PathVariable Long noticeId
    ) {
        inoticeService.deleteNotice(noticeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteNoticeSuccess));
    }

    /**
     * Notifies users about stock alerts.
     *
     * <p>This endpoint allows an admin to notify users about stock alerts for a specific item.
     * The item ID must be specified in the path.</p>
     *
     * @param itemId the ID of the item
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notification
     * @success Users successfully notified about stock alerts
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException The item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/notices/items/{itemId}/notify-stock-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> notifyStockAlertToUsers(
            @PathVariable Long itemId
    ) {
        inoticeService.notifyStockAlertToUsers(itemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_NotifyUsersSuccess));
    }

    /**
     * Notifies users if cart items are invalidated.
     *
     * <p>This endpoint allows an admin to notify users if their cart items are invalidated due to stock issues.
     * The item ID must be specified in the path.</p>
     *
     * @param itemId the ID of the item
     * @return ResponseEntity<ResponseDto> Response object containing the result of the notification
     * @success Users successfully notified about cart item invalidation
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws NotFoundException The item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/notices/items/{itemId}/notify-cart-item-invalidation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> notifyCartItemInvalidationToUsers(
            @PathVariable Long itemId
    ) {
        inoticeService.notifyCartItemInvalidationToUsers(itemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_NotifyUsersSuccess));
    }

    /**
     * Retrieves a paginated list of all notices for a specific user.
     *
     * <p>This endpoint allows users to retrieve a list of notices assigned to them with pagination support.
     * The user ID and pagination parameters (page, size) must be specified.</p>
     *
     * @param userId The ID of the user whose notices are being retrieved
     * @param page The page number to retrieve (optional, default is 0)
     * @param size The number of items per page (optional, default is 10)
     * @return Paginated list of NoticeResponseDto objects
     * Response Code: 200
     * @throws TypeMismatchException Invalid query parameter types
     * Response Code: 400
     * @throws AccessDeniedException Unauthorized access
     * Response Code: 403
     * @throws NotFoundException User not found
     * Response Code: 404
     * @throws Exception Internal server error
     * Response Code: 500
     */
    @HasId
    @GetMapping("/users/{userId}/notices")
    public ResponseEntity<PaginatedResponseDto<NoticeResponseDto>> getAllUserNotices(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<NoticeResponseDto> notices = inoticeService.getAllNotices(userId, pageable);
        PaginatedResponseDto<NoticeResponseDto> response = PaginatedResponseDto.of(notices);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves the details of a specific notice for a user.
     *
     * <p>This endpoint allows users to retrieve details of a specific notice by providing the user ID and notice ID.</p>
     *
     * @param userId The ID of the user
     * @param noticeId The ID of the notice
     * @return NoticeDetailsResponseDto containing detailed notice information
     * Response Code: 200
     * @throws TypeMismatchException Invalid path variable types
     * Response Code: 400
     * @throws AccessDeniedException Unauthorized access
     * Response Code: 403
     * @throws NotFoundException User or notice not found
     * Response Code: 404
     * @throws Exception Internal server error
     * Response Code: 500
     */
    @HasId
    @GetMapping("/users/{userId}/notices/{noticeId}")
    public ResponseEntity<NoticeDetailsResponseDto> getNoticeDetails(
            @PathVariable Long userId,
            @PathVariable Long noticeId
    ){
        NoticeDetailsResponseDto noticeDetail = inoticeService.getNoticeDetails(userId, noticeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(noticeDetail);
    }
}

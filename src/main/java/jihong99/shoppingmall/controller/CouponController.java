package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.CouponRequestDto;
import jihong99.shoppingmall.dto.request.PatchCouponRequestDto;
import jihong99.shoppingmall.dto.response.*;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CouponController {
    private final ICouponService icouponService;

    /**
     * Creates a new coupon.
     *
     * <p>This endpoint allows an admin to create a new coupon. The request body must contain valid coupon details.</p>
     *
     * @param couponRequestDto DTO object containing necessary information for coupon creation
     * @return ResponseEntity<ResponseDto> Response object containing the result of the coupon creation
     * @success Coupon successfully created
     * Response Code: 201
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createCoupon(@RequestBody @Valid CouponRequestDto couponRequestDto) {
        icouponService.createCoupon(couponRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createCoupon));
    }

    /**
     * Updates an existing coupon.
     *
     * <p>This endpoint allows an admin to update an existing coupon. The coupon ID must be specified in the path,
     * and the request body must contain valid coupon details.</p>
     *
     * @param couponId the ID of the coupon to be updated
     * @param patchCouponRequestDto DTO object containing necessary information for coupon update
     * @return ResponseEntity<ResponseDto> Response object containing the result of the coupon update
     * @success Coupon successfully updated
     * Response Code: 200
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws NotFoundException The coupon is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PatchMapping("/admin/coupons/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> updateCoupon(
            @RequestBody @Valid PatchCouponRequestDto patchCouponRequestDto,
            @PathVariable Long couponId) {
        icouponService.patchCoupon(couponId, patchCouponRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateCouponSuccess));
    }

    /**
     * Deletes a coupon.
     *
     * <p>This endpoint allows an admin to delete an existing coupon. The coupon ID must be specified in the path.</p>
     *
     * @param couponId the ID of the coupon to be deleted
     * @return ResponseEntity<ResponseDto> Response object containing the result of the coupon deletion
     * @success Coupon successfully deleted
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws NotFoundException The coupon is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/admin/coupons/{couponId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteCoupon(@PathVariable Long couponId) {
        icouponService.deleteCoupon(couponId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteCouponSuccess));
    }

    /**
     * Assigns a coupon to a specific user.
     *
     * <p>This endpoint allows an admin to assign a specific coupon to a specific user.
     * The coupon ID and user ID must be specified in the path.</p>
     *
     * @param couponId the ID of the coupon to be assigned
     * @param userId the ID of the user to whom the coupon will be assigned
     * @return ResponseEntity<ResponseDto> Response object containing the result of the coupon assignment
     * @success Coupon successfully assigned to user
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws NotFoundException Coupon or user not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/coupons/{couponId}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> assignCouponToUser(
            @PathVariable Long couponId,
            @PathVariable Long userId) {
        icouponService.distributeCouponToUser(couponId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Assigns a coupon to all users.
     *
     * <p>This endpoint allows an admin to assign a specific coupon to all users. The coupon ID must be specified in the path.</p>
     *
     * @param couponId the ID of the coupon to be assigned to all users
     * @return ResponseEntity<ResponseDto> Response object containing the result of the coupon assignment
     * @success Coupon successfully assigned to all users
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws NotFoundException The coupon is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/coupons/{couponId}/assign/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> assignCouponToAll(
            @PathVariable Long couponId) {
        icouponService.distributeCouponToAllUsers(couponId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Retrieves all coupons.
     *
     * <p>This endpoint allows an admin to retrieve a paginated list of all coupons.</p>
     *
     * @param page the page number to retrieve (for pagination)
     * @param size the number of coupons to retrieve per page (maximum 10)
     * @return ResponseEntity<PaginatedResponseDto<CouponResponseDto>> a paginated response containing the coupons
     * @success Valid response containing the paginated coupons
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<CouponResponseDto>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CouponResponseDto> coupons = icouponService.getAllCoupons(pageable);
        PaginatedResponseDto<CouponResponseDto> response = PaginatedResponseDto.of(coupons);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves all coupons for a specific user.
     *
     * <p>This endpoint allows an admin to retrieve a paginated list of all coupons assigned to a specific user.</p>
     *
     * @param userId the ID of the user whose coupons are being requested
     * @param page the page number to retrieve (for pagination)
     * @param size the number of coupons to retrieve per page (maximum 10)
     * @return ResponseEntity<PaginatedResponseDto<UserCouponDetailsResponseDto>> a paginated response containing the user's coupons
     * @success Valid response containing the paginated user coupons
     * Response Code: 200
     * @throws TypeMismatchException Method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws NotFoundException The user is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/admin/coupons/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<UserCouponsResponseDto>> getAllUserCoupons(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserCouponsResponseDto> coupons = icouponService.getUserCoupons(userId, pageable);
        PaginatedResponseDto<UserCouponsResponseDto> response = PaginatedResponseDto.of(coupons);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}

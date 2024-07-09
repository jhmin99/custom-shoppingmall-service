package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.dto.CouponResponseDto;
import jihong99.shoppingmall.dto.PaginatedResponseDto;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.entity.enums.Tiers;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.InvalidExpirationDateException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.ICouponService;
import jihong99.shoppingmall.utils.annotation.HasId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_200_DistributeCouponSuccess;
import static jihong99.shoppingmall.constants.Constants.STATUS_200;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {
    private final ICouponService icouponService;

    /**
     * Creates a new coupon.
     *
     * <p>This endpoint allows an admin user to create a new coupon. It validates the coupon details,
     * checks for duplicate coupon names, and ensures the expiration date is valid.</p>
     *
     * @param couponRequestDto the data transfer object containing the coupon details
     * @return the created coupon response
     * @success Coupon object successfully created
     * Response Code: 201
     * @exception MethodArgumentNotValidException Validation failed for the request body
     * Response Code: 400
     * @exception DuplicateNameException Duplicate coupon name exists
     * Response Code: 400
     * @exception InvalidExpirationDateException Expiration date is in the past
     * Response Code: 400
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PostMapping("/admin/coupon")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponResponseDto> createCoupon(@RequestBody @Valid CouponRequestDto couponRequestDto) {
        CouponResponseDto couponResponseDto = icouponService.createCoupon(couponRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(couponResponseDto);
    }

    /**
     * Retrieves the user's coupons with pagination.
     *
     * @param userId the user's id
     * @param page the page number
     * @param size the page size
     * @return a paginated list of the user's coupons
     * @success Valid response containing the paginated user coupons
     * Response Code: 200
     * @exception NotFoundException Thrown if the user is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the specified userId
     */
    @GetMapping("/users/coupons")
    @HasId
    public ResponseEntity<PaginatedResponseDto<CouponResponseDto>> getUserCoupons(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CouponResponseDto> userCoupons = icouponService.getUserCoupons(userId, pageable);
        PaginatedResponseDto<CouponResponseDto> response = PaginatedResponseDto.of(userCoupons);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves all coupons with pagination.
     *
     * @param page the page number
     * @param size the page size
     * @return a paginated list of all coupons
     * @success Valid response containing the paginated list of all coupons
     * Response Code: 200
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @GetMapping("/admin/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<CouponResponseDto>> getAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CouponResponseDto> allCoupons = icouponService.getAllCoupons(pageable);
        PaginatedResponseDto<CouponResponseDto> response = PaginatedResponseDto.of(allCoupons);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Distributes a coupon to a specific user.
     *
     * @param couponId the ID of the coupon
     * @param userId the ID of the user
     * @return a response indicating the result of the distribution
     * @success Coupon successfully distributed to the specified user
     * Response Code: 200
     * @exception NotFoundException Thrown if the user or coupon is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PostMapping("/admin/coupons/distribute/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> distributeCouponToUser(@RequestParam Long couponId, @RequestParam Long userId) {
        icouponService.distributeCouponToUser(couponId, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Distributes a coupon to users by tier.
     *
     * @param couponId the ID of the coupon
     * @param tier the tier of users
     * @return a response indicating the result of the distribution
     * @success Coupon successfully distributed to the specified tier of users
     * Response Code: 200
     * @exception NotFoundException Thrown if the coupon is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PostMapping("/admin/coupons/distribute/tier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> distributeCouponToUsersByTier(@RequestParam Long couponId, @RequestParam Tiers tier) {
        icouponService.distributeCouponToUsersByTier(couponId, tier);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Distributes a coupon to all users.
     *
     * @param couponId the ID of the coupon
     * @return a response indicating the result of the distribution
     * @success Coupon successfully distributed to all users
     * Response Code: 200
     * @exception NotFoundException Thrown if the coupon is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PostMapping("/admin/coupons/distribute/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> distributeCouponToAllUsers(@RequestParam Long couponId) {
        icouponService.distributeCouponToAllUsers(couponId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DistributeCouponSuccess));
    }
}

package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.response.order.OrderDetailsResponseDto;
import jihong99.shoppingmall.dto.response.shared.PaginatedResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IOrderService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    private final IOrderService iorderService;


    /**
     * Retrieves paginated user order details.
     *
     * <p>This endpoint allows an admin to retrieve paginated order details for a specific user.
     * The request requires an `Authorization` header with a valid bearer token.</p>
     *
     * @param userId The ID of the user whose orders are being retrieved
     * @param page The page number to retrieve (optional, default is 0)
     * @param size The number of orders to retrieve per page (optional, default is 10)
     * @return ResponseEntity<PaginatedResponseDto<OrderDetailsResponseDto>> Response object containing paginated order details
     * @success Valid response containing paginated order details
     * Response Code: 200
     * @throws TypeMismatchException Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have ADMIN role
     * Response Code: 403
     * @throws NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/admin/orders/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<OrderDetailsResponseDto>> getAllOrderDetails(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<OrderDetailsResponseDto> orderDetails = iorderService.getUserOrderDetails(userId, pageable);
        PaginatedResponseDto<OrderDetailsResponseDto> response = PaginatedResponseDto.of(orderDetails);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}

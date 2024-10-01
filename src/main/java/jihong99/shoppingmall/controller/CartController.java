package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.cart.CartItemRequestDto;
import jihong99.shoppingmall.dto.request.cart.UpdateQuantityRequestDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.ICartService;
import jihong99.shoppingmall.utils.annotation.HasId;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class CartController {

    private final ICartService icartService;

    /**
     * Adds an item to the user's cart.
     *
     * <p>This endpoint allows a user to add a specific item to their cart. The request body must
     * contain the quantity of the item being added.</p>
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item to be added to the cart
     * @param cartItemRequestDto DTO containing the quantity of the item
     * @return ResponseEntity<ResponseDto> Response object containing the result of the item addition
     * @success Item successfully added to the cart
     * Response Code: 201
     * @throws MethodArgumentNotValidException Thrown if validation for the request body fails
     * Response Code: 400
     * @throws TypeMismatchException Thrown if method argument (path variable) cannot be converted to the expected type
     * Response Code: 400
     * @throws InvalidOperationException Thrown if the item cannot be added to the cart for certain reasons (e.g., invalid item)
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required permissions
     * Response Code: 403
     * @throws NotFoundException Thrown if the user or item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/users/{userId}/cart/{itemId}")
    @HasId
    public ResponseEntity<ResponseDto> addItemToCart(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto
    ) {
        icartService.addCartItem(userId, itemId, cartItemRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_AddItemToCartSuccess));
    }

    /**
     * Updates the quantity of an item in the user's cart.
     *
     * <p>This endpoint allows a user to update the quantity of a specific item in their cart.
     * The request body must contain the updated quantity.</p>
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item whose quantity will be updated
     * @param updateQuantityRequestDto DTO containing the updated quantity
     * @return ResponseEntity<ResponseDto> Response object containing the result of the quantity update
     * @success Quantity successfully updated
     * Response Code: 200
     * @throws MethodArgumentNotValidException Thrown if validation for the request body fails
     * Response Code: 400
     * @throws TypeMismatchException Thrown if method argument (path variable) cannot be converted to the expected type
     * Response Code: 400
     * @throws InvalidOperationException Thrown if the item cannot be updated (e.g., out of stock)
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required permissions
     * Response Code: 403
     * @throws NotFoundException Thrown if the user or item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PatchMapping("/users/{userId}/cart/{itemId}")
    @HasId
    public ResponseEntity<ResponseDto> updateItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateQuantityRequestDto updateQuantityRequestDto
    ) {
        icartService.updateItemQuantity(userId, itemId, updateQuantityRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateQuantitySuccess));
    }

    /**
     * Removes an item from the user's cart.
     *
     * <p>This endpoint allows a user to remove a specific item from their cart.</p>
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item to be removed from the cart
     * @return ResponseEntity<ResponseDto> Response object containing the result of the item removal
     * @success Item successfully removed from the cart
     * Response Code: 200
     * @throws TypeMismatchException Thrown if method argument (path variable) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required permissions
     * Response Code: 403
     * @throws NotFoundException Thrown if the user or item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/users/{userId}/cart/{itemId}")
    @HasId
    public ResponseEntity<ResponseDto> removeItemFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ) {
        icartService.removeCartItem(userId, itemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_RemoveItemFromCartSuccess));
    }
}
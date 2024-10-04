package jihong99.shoppingmall.controller;

import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IWishListService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class WishListController {

    private final IWishListService iWishListService;
    /**
     * Adds an item to the user's wishlist.
     *
     * <p>This endpoint allows the user to add a specific item to their wishlist. The user
     * must have a valid API key and authorization token.</p>
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item to be added to the wishlist
     * @return ResponseEntity<ResponseDto> Response object containing the result of the addition
     * @success Item successfully added to the wishlist
     * Response Code: 201
     * @throws TypeMismatchException Thrown if the method argument (path variable) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required permissions
     * Response Code: 403
     * @throws NotFoundException Thrown if the user or item is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/users/{userId}/wishlists/{itemId}")
    public ResponseEntity<ResponseDto> addItemToWishList(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ){
        iWishListService.addWishItem(userId, itemId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_AddItemToWishListSuccess));
    }

    /**
     * Removes an item from the user's wishlist.
     *
     * <p>This endpoint allows the user to remove a specific item from their wishlist.</p>
     *
     * @param userId The ID of the user
     * @param itemId The ID of the item to be removed from the wishlist
     * @return ResponseEntity<ResponseDto> Response object containing the result of the removal
     * @success Item successfully removed from the wishlist
     * Response Code: 200
     * @throws TypeMismatchException Thrown if the method argument (path variable) cannot be converted to the expected type
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required permissions
     * Response Code: 403
     * @throws NotFoundException Thrown if the user or wishItem is not found
     * Response Code: 404
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/users/{userId}/wishlists/{itemId}")
    public ResponseEntity<ResponseDto> removeItemFromWishList(
            @PathVariable Long userId,
            @PathVariable Long itemId
    ){
        iWishListService.removeWishItem(userId, itemId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_RemoveItemFromWishListSuccess));
    }
}

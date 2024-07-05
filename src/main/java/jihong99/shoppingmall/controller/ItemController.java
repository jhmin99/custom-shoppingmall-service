package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

    private final IItemService iitemService;
    /**
     * Create a new item.
     *
     * <p>This endpoint allows an admin to create a new item. The request body must contain valid item details.</p>
     *
     * @param itemRequestDto DTO object containing necessary information for item creation
     * @return ResponseEntity<Object> Response object containing the result of the item creation
     * @success Item successfully created
     * Response Code: 201
     * @exception MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @exception NotFoundException Thrown if the category for the item is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PostMapping("/admin/item")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponseDto> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        ItemResponseDto createdItem = iitemService.createItem(itemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
    /**
     * Update an existing item.
     *
     * <p>This endpoint allows an admin to update an existing item. The request body must contain valid item details
     * and the item ID must be specified in the path.</p>
     *
     * @param itemId the ID of the item to be updated
     * @param itemRequestDto DTO object containing necessary information for item update
     * @return ResponseEntity<ResponseDto> Response object containing the result of the item update
     * @success Item successfully updated
     * Response Code: 200
     * @exception MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @exception NotFoundException Thrown if the item is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @PutMapping("/admin/item/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> updateItem(@PathVariable Long itemId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        iitemService.updateItem(itemId, itemRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateItemSuccess));
    }
    /**
     * Delete an existing item.
     *
     * <p>This endpoint allows an admin to delete an existing item. The item ID must be specified in the path.</p>
     *
     * @param itemId the ID of the item to be deleted
     * @return ResponseEntity<ResponseDto> Response object containing the result of the item deletion
     * @success Item successfully deleted
     * Response Code: 200
     * @exception NotFoundException Thrown if the item is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     * @precondition The authenticated user must have the 'ADMIN' role
     */
    @DeleteMapping("/admin/item/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteItem(@PathVariable Long itemId) {
        iitemService.deleteItem(itemId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteItemSuccess));
    }
}

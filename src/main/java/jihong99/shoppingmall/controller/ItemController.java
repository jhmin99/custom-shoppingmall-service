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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


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
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        ItemResponseDto createdItem = iitemService.createItem(itemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }
}

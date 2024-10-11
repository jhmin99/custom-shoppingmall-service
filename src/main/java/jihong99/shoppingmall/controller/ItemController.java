package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.item.ItemRequestDto;
import jihong99.shoppingmall.dto.request.item.PatchItemRequestDto;
import jihong99.shoppingmall.dto.request.item.UpdateStockRequestDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.ImageUploadException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IItemService;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static jihong99.shoppingmall.constants.Constants.*;
import static org.springframework.http.MediaType.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ItemController {

    private final IItemService iitemService;

    /**
     * Handles the creation of a new item. The item details and images are expected as part of a multipart/form-data request.
     *
     * <p>This method processes a request to create a new item, including validating the input data, uploading and associating images,
     * and saving the item in the database. The method expects the item details to be provided as a JSON object and the images as a list of multipart files.</p>
     *
     * @param itemRequestDto DTO containing item details such as name, price, stock, and categories
     * @param images List of images to associate with the item
     * @return A ResponseEntity containing the status of the creation operation
     *
     * @success Item successfully created
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws IllegalArgumentException if an unknown storage type is specified
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if any of the specified categories do not exist
     * Response Code: 404
     *
     * @throws ImageUploadException if an error occurs while uploading images
     * Response Code: 500
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping(path = "/admin/items", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createItem(
            @Valid @RequestPart("item") ItemRequestDto itemRequestDto,
            @RequestPart("images") List<MultipartFile> images
            ) {
        iitemService.createItem(itemRequestDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(
             new ResponseDto(STATUS_201, MESSAGE_201_createItem)
        );
    }

    /**
     * Handles the update of an existing item. The item details and images are expected as part of a multipart/form-data request.
     *
     * <p>This method processes a request to update an existing item, including validating the input data, uploading new images,
     * removing specified images, and saving the updated item in the database. The method expects the item details to be provided
     * as a JSON object and the images as a list of multipart files.</p>
     *
     * @param itemId The ID of the item to be updated
     * @param patchItemRequestDto DTO containing updated item details such as name, price, and categories
     * @param addImages List of new images to associate with the item
     * @param removeImageIds List of image IDs to remove from the item
     * @return A ResponseEntity containing the status of the update operation
     *
     * @success Item successfully updated
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the itemId path variable cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws IllegalArgumentException if an unknown storage type is specified
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the specified item, categories, or images do not exist
     * Response Code: 404
     *
     * @throws ImageUploadException if an error occurs while uploading images
     * Response Code: 500
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping(path= "/admin/items/{itemId}", consumes = MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> patchItem(
            @PathVariable Long itemId,
            @Valid @RequestPart("item") PatchItemRequestDto patchItemRequestDto,
            @RequestPart(value = "addImages", required = false) List<MultipartFile> addImages,
            @RequestPart(value = "removeImageIds", required = false) List<Long> removeImageIds
    ) {
        iitemService.patchItem(itemId, patchItemRequestDto, addImages, removeImageIds);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto(STATUS_200, MESSAGE_200_UpdateItemSuccess)
        );
    }

    /**
     * Marks an item as invalid. The item is identified by its ID.
     *
     * <p>This endpoint is used by admins to mark an item as invalid, effectively making it unavailable for purchase.
     * The item ID is provided as a path variable.</p>
     *
     * @param itemId The ID of the item to be marked as invalid
     * @return A ResponseEntity containing the status of the operation
     *
     * @success Item successfully marked as invalid
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (itemId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the item is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping("/admin/items/{itemId}/invalid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> invalidateItem(@PathVariable Long itemId) {
        iitemService.markItemAsInvalid(itemId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto(STATUS_200, MESSAGE_200_InvalidateItemSuccess)
        );
    }

    /**
     * Marks an item as valid. The item is identified by its ID.
     *
     * <p>This endpoint is used by admins to mark an item as valid, making it available for purchase.
     * The item ID is provided as a path variable.</p>
     *
     * @param itemId The ID of the item to be marked as valid
     * @return A ResponseEntity containing the status of the operation
     *
     * @success Item successfully marked as valid
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (itemId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the item is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping( "/admin/items/{itemId}/valid")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> validateItem(@PathVariable Long itemId) {
        iitemService.markItemAsValid(itemId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto(STATUS_200, MESSAGE_200_UpdateItemSuccess)
        );
    }

    /**
     * Updates the stock levels of an item. The item is identified by its ID, and the new stock level is provided in the request body.
     *
     * <p>This endpoint is used by admins to update the stock quantity of an item. The item ID is provided as a path variable,
     * and the new stock level is provided in the request body as a JSON object.</p>
     *
     * @param itemId The ID of the item whose stock level is being updated
     * @param updateStockRequestDto DTO containing the new stock level
     * @return A ResponseEntity containing the status of the operation
     *
     * @success Stock level successfully updated
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., negative stock level)
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (itemId) cannot be converted to the expected type
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the 'ADMIN' role
     * Response Code: 403
     *
     * @throws NotFoundException if the item is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PatchMapping(path="/admin/items/{itemId}/stock", consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> updateStock(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateStockRequestDto updateStockRequestDto) {
        iitemService.updateItemStock(itemId, updateStockRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseDto(STATUS_200, MESSAGE_200_UpdateItemSuccess)
        );
    }
}

package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.CategoryRequestDto;
import jihong99.shoppingmall.dto.request.PutCategoryRequestDto;
import jihong99.shoppingmall.dto.response.CategoryResponseDto;
import jihong99.shoppingmall.dto.response.PaginatedResponseDto;
import jihong99.shoppingmall.dto.response.ResponseDto;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.ICategoryService;
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
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService icategoryService;

    /**
     * Creates a new category.
     *
     * <p>This endpoint allows an admin to create a new category. The request body must contain
     * the necessary details for the category.</p>
     *
     * @param categoryRequestDto DTO object containing the category details
     * @return ResponseEntity<ResponseDto> Response object containing the result of the category creation
     * @success Category successfully created
     * Response Code: 201
     * @throws DuplicateNameException Duplicate name exists
     * Response Code: 400
     * @throws MethodArgumentNotValidException Validation failed
     * Response Code: 400
     * @throws AccessDeniedException Thrown if the user does not have the required ID
     * Response Code: 403
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        icategoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createCategory));
    }

    /**
     * Retrieves a paginated list of categories.
     *
     * <p>This endpoint retrieves a list of categories with pagination support. The default page
     * number is 0 and the default page size is 10.</p>
     *
     * @param page the page number to retrieve (for pagination)
     * @param size the number of categories to retrieve per page (maximum 10)
     * @return ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> Response object containing the paginated list of categories
     * @success Valid response containing the paginated list of categories
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws AccessDeniedException           Thrown if the user does not have ADMIN role
     *                                         Response Code: 403
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @GetMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponseDto> categories = icategoryService.getCategories(pageable);
        PaginatedResponseDto<CategoryResponseDto> response = PaginatedResponseDto.of(categories);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Updates a category.
     *
     * <p>This endpoint allows an admin to update an existing category.</p>
     *
     * @param categoryId The ID of the category to update
     * @param putCategoryRequestDto DTO object containing the updated category details
     * @return ResponseEntity<ResponseDto> Response object containing the result of the category update
     * @success Category successfully updated
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws NotFoundException Category not found
     * Response Code: 404
     * @throws AccessDeniedException Thrown if the user does not have ADMIN role
     * Response Code: 403
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @PutMapping("/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> editCategory(@PathVariable Long categoryId, @Valid @RequestBody PutCategoryRequestDto putCategoryRequestDto) {
        icategoryService.editCategory(categoryId, putCategoryRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateCategorySuccess));
    }

    /**
     * Deletes a category.
     *
     * <p>This endpoint allows an admin to delete an existing category.</p>
     *
     * @param categoryId The ID of the category to delete
     * @return ResponseEntity<ResponseDto> Response object containing the result of the category deletion
     * @success Category successfully deleted
     * Response Code: 200
     * @throws TypeMismatchException           Thrown if method argument (path variable or query parameter) cannot be converted to the expected type
     *                                         Response Code: 400
     * @throws NotFoundException Category not found
     * Response Code: 404
     * @throws HasRelatedEntitiesException Relation conflict, cannot delete category
     * Response Code: 409
     * @throws AccessDeniedException Thrown if the user does not have ADMIN role
     * Response Code: 403
     * @throws Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto> deleteCategory(@PathVariable Long categoryId) {
        icategoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteCategorySuccess));
    }
}



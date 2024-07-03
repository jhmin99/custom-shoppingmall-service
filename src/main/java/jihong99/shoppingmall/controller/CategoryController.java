package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.dto.CategoryResponseDto;
import jihong99.shoppingmall.dto.PaginatedResponseDto;
import jihong99.shoppingmall.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_200_fetchSuccess;
import static jihong99.shoppingmall.constants.Constants.STATUS_200;

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
 * @return ResponseEntity<CategoryResponseDto> Response object containing the created category
 * @success Category successfully created
 * Response Code: 201
 * @precondition The user must have the 'ADMIN' role
 * Response Code: 403
 * @exception MethodArgumentNotValidException Validation failed
 * Response Code: 400
 * @exception Exception Internal server error occurred
 * Response Code: 500
 */
    @PostMapping("/admin/category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto createdCategory = icategoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdCategory);
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
 * @exception Exception Internal server error occurred
 * Response Code: 500
 */
    @GetMapping("/categories")
    public ResponseEntity<PaginatedResponseDto<CategoryResponseDto>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryResponseDto> categories = icategoryService.getCategories(pageable);
        PaginatedResponseDto<CategoryResponseDto> response = PaginatedResponseDto.of(categories, STATUS_200, MESSAGE_200_fetchSuccess);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}

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
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_200_fetchSuccess;
import static jihong99.shoppingmall.constants.Constants.STATUS_200;

@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService icategoryService;


    @PostMapping("/admin/category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto createdCategory = icategoryService.createCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdCategory);
    }

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

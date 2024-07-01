package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.dto.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    Page<CategoryResponseDto> getCategories(Pageable pageable);
}

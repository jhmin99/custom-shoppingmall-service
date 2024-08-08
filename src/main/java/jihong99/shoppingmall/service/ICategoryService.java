package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.CategoryRequestDto;
import jihong99.shoppingmall.dto.request.PutCategoryRequestDto;
import jihong99.shoppingmall.dto.response.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    void createCategory(CategoryRequestDto categoryRequestDto);
    Page<CategoryResponseDto> getCategories(Pageable pageable);
    void editCategory(Long id, PutCategoryRequestDto putCategoryRequestDto);
    void deleteCategory(Long id);
}

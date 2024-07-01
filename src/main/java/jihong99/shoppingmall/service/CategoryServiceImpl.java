package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.dto.CategoryResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.mapper.CategoryMapper;
import jihong99.shoppingmall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing categories.
 *
 * <p>This class handles the business logic related to categories, including
 * creating new categories and retrieving paginated lists of categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Creates a new category.
     *
     * <p>This method maps a {@link CategoryRequestDto} to a {@link Category} entity,
     * saves the entity to the repository, and returns a response DTO.</p>
     *
     * @param categoryRequestDto the DTO containing the category details
     * @return the response DTO containing the saved category details
     */
    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
        CategoryMapper categoryMapper = new CategoryMapper();
        Category category = categoryMapper.mapToCategory(categoryRequestDto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDto.of(savedCategory.getId(), savedCategory.getName());
    }

    /**
     * Retrieves a paginated list of categories.
     *
     * <p>This method fetches a page of categories from the repository and maps
     * each category entity to a response DTO.</p>
     *
     * @param pageable the pagination information
     * @return a page of category response DTOs
     */
    @Override
    public Page<CategoryResponseDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(category -> CategoryResponseDto.of(category.getId(), category.getName()));
    }
}

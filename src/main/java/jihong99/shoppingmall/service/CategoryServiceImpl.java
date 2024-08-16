package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.request.CategoryRequestDto;
import jihong99.shoppingmall.dto.request.PutCategoryRequestDto;
import jihong99.shoppingmall.dto.response.CategoryResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.HasRelatedEntitiesException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CategoryItemRepository;
import jihong99.shoppingmall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static jihong99.shoppingmall.constants.Constants.*;

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
    private final CategoryItemRepository categoryItemRepository;

    /**
     * Creates a new category.
     *
     * <p>This method maps a {@link CategoryRequestDto} to a {@link Category} entity,
     * saves the entity to the repository, and returns a response DTO.</p>
     *
     * @param categoryRequestDto the DTO containing the category details
     */
    @Override
    @Transactional
    public void createCategory(CategoryRequestDto categoryRequestDto) {
        Optional<Category> findCategory = categoryRepository.findByName(categoryRequestDto.getName());
        if (findCategory.isPresent()){
            throw new DuplicateNameException(MESSAGE_400_duplicatedName);
        }
        Category category = getCategory(categoryRequestDto);
        categoryRepository.save(category);
    }

    private static Category getCategory(CategoryRequestDto categoryRequestDto) {
        return Category.builder()
                .name(categoryRequestDto.getName())
                .build();
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

    /**
     * Updates a category.
     *
     * <p>This method updates the name of an existing category.</p>
     *
     * @param categoryId the ID of the category to update
     * @param putCategoryRequestDto the DTO containing the updated category details
     */
    @Override
    @Transactional
    public void editCategory(Long categoryId, PutCategoryRequestDto putCategoryRequestDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_CategoryNotFound));
        category.updateName(putCategoryRequestDto.getName());
        categoryRepository.save(category);
    }

    /**
     * Deletes a category.
     *
     * <p>This method deletes an existing category if it has no related items.</p>
     *
     * @param categoryId the ID of the category to delete
     */
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_CategoryNotFound));
        if(categoryItemRepository.findByCategoryId(categoryId).isEmpty()){
            categoryRepository.delete(category);
        } else {
            throw new HasRelatedEntitiesException(MESSAGE_409_RelationConflict);
        }
    }
}


package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.dto.CategoryResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.repository.CategoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ICategoryService categoryService;
    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){
        categoryRepository.deleteAll();
    }

    @Test
    void createCategory() {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        // when
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(categoryRequestDto);
        // then
        assertThat(categoryResponseDto.getId()).isNotNull();
        assertThat(categoryResponseDto.getName()).isEqualTo("Sample Category");
    }

    @Test
    @Transactional
    void getCategories() {
        // given
        Category category1 = new Category(null,"Category 1");
        Category category2 = new Category(null,"Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CategoryResponseDto> categories = categoryService.getCategories(pageable);

        // then
        assertThat(categories).isNotNull();
        assertThat(categories.getTotalElements()).isEqualTo(2);
        assertThat(categories.getContent()).hasSize(2);

        CategoryResponseDto categoryDto1 = categories.getContent().get(0);
        assertThat(categoryDto1.getName()).isEqualTo("Category 1");
        assertThat(categoryDto1.getId()).isNotNull();

        CategoryResponseDto categoryDto2 = categories.getContent().get(1);
        assertThat(categoryDto2.getName()).isEqualTo("Category 2");
        assertThat(categoryDto2.getId()).isNotNull();
    }
}
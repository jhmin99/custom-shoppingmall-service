package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.entity.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CategoryMapperTest {

    @Test
    void mapToCategory() {
        // given
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto("Sample Category");
        // when
        CategoryMapper categoryMapper = new CategoryMapper();
        Category category = categoryMapper.mapToCategory(categoryRequestDto);
        // then
        assertThat(category.getName()).isEqualTo("Sample Category");
    }
}
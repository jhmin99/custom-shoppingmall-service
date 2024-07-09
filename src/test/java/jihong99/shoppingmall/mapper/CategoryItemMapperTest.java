package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.entity.CategoryItem;
import jihong99.shoppingmall.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
@ActiveProfiles("test")
class CategoryItemMapperTest {

    @Test
    void mapToCategoryItem() {
        // given
        Item item = new Item(null, "Sample Item", 100, 50, "#sample#sample2", LocalDate.now());
        Category category = new Category(null, "Sample Category");
        // when
        CategoryItemMapper categoryItemMapper = new CategoryItemMapper();
        CategoryItem categoryItem = categoryItemMapper.mapToCategoryItem(item,category);
        // then
        assertThat(categoryItem.getCategory()).isEqualTo(category);
        assertThat(categoryItem.getItem()).isEqualTo(item);
    }
}
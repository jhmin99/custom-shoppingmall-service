package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.entity.CategoryItem;
import jihong99.shoppingmall.entity.Item;

public class CategoryItemMapper {
    public CategoryItem mapToCategoryItem(Item item, Category category){
        CategoryItem categoryItem = CategoryItem.builder()
                .item(item)
                .category(category)
                .build();
        return categoryItem;
    }
}

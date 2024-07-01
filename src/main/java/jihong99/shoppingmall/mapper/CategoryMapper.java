package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.CategoryRequestDto;
import jihong99.shoppingmall.entity.Category;

public class CategoryMapper {

    public Category mapToCategory(CategoryRequestDto categoryRequestDto){
        return Category.builder()
                .name(categoryRequestDto.getName())
                .build();
    }
}

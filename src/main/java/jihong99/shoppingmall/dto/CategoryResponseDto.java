package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;

    public static CategoryResponseDto of(Long id, String name) {
        return new CategoryResponseDto(id, name);
    }
}
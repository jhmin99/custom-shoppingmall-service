package jihong99.shoppingmall.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class PatchItemRequestDto {
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private String name;

    @Min(value = 0, message = "Price must be greater than or equal to 0.")
    private Integer price;

    @Min(value = 0, message = "Stock must be greater than or equal to 0.")
    private Integer stock;

    @Size(min = 3, max = 50, message = "Keyword must be between 3 and 50 characters.")
    @Pattern(regexp = "^#(\\w+)(#\\w+)*$", message = "Keyword must start with # and be followed by words separated by #.")
    private String keyword;

    private List<Long> categoryIds;
}

package jihong99.shoppingmall.dto.request.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PutCategoryRequestDto {
    @NotNull(message = "Name is a required field.")
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
    private String name;
}

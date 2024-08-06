package jihong99.shoppingmall.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {

    @NotNull(message = "name is a required field.")
    @Size(min = 5, max = 20, message = "name must be between 5 and 20 characters.")
    private String name;
}
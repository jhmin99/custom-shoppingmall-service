package jihong99.shoppingmall.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartItemRequestDto {
    @NotNull(message = "Quantity is a required field.")
    @Min(value = 1, message = "Quantity must be greater than or equal to 1.")
    private Integer quantity;
}

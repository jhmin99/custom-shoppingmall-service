package jihong99.shoppingmall.dto.request.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateStockRequestDto {
    
    @NotNull(message = "Stock is a required field.")
    @Min(value = 0, message = "Stock must be greater than or equal to 0.")
    private Integer stock;
}

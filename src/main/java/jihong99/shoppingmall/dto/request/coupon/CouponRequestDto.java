package jihong99.shoppingmall.dto.request.coupon;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class CouponRequestDto {
    @NotNull(message = "Discount type is a required field.")
    private DiscountType discountType;

    @NotNull(message = "Discount value is a required field.")
    @Min(value = 0, message = "Discount value must be greater than or equal to 0.")
    private Long discountValue;

    @NotNull(message = "Expiration date is a required field.")
    @Future(message = "Expiration date must be in the future.")
    private LocalDate expirationDate;
}

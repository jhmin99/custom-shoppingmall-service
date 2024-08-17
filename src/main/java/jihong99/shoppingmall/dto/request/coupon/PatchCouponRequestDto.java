package jihong99.shoppingmall.dto.request.coupon;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class PatchCouponRequestDto {
    private DiscountType discountType;

    @Min(value = 0, message = "Discount value must be greater than or equal to 0.")
    private Long discountValue;

    @Future(message = "Expiration date must be in the future.")
    private LocalDate expirationDate;
}

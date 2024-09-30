package jihong99.shoppingmall.dto.response.coupon;

import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CouponSummaryResponseDto {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Long discountValue;
    private LocalDate expirationDate;

    public static CouponSummaryResponseDto of(Long id, String code, DiscountType discountType, Long discountValue, LocalDate expirationDate){
        return new CouponSummaryResponseDto(id, code, discountType, discountValue, expirationDate);
    }
}

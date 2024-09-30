package jihong99.shoppingmall.dto.response.coupon;

import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponDetailsResponseDto {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Long discountValue;
    private boolean isValid;
    private boolean isUsed;


    public static CouponDetailsResponseDto of(Long id, String code, DiscountType discountType, Long discountValue, boolean isValid, boolean isUsed){
        return new CouponDetailsResponseDto(id, code, discountType, discountValue, isValid, isUsed);
    }
}

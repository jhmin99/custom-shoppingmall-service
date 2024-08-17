package jihong99.shoppingmall.dto.response.coupon;

import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CouponResponseDto {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Long discountValue;
    private LocalDate expirationDate;
    private Timestamp creationTime;
    private Timestamp lastModifiedTime;

    public static CouponResponseDto of(Long id, String code, DiscountType discountType, Long discountValue, LocalDate expirationDate, Timestamp creationTime, Timestamp lastModifiedTime){
        return new CouponResponseDto(id, code, discountType, discountValue, expirationDate, creationTime, lastModifiedTime);
    }
}

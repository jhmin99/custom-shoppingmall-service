package jihong99.shoppingmall.dto.response.coupon;

import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class UserCouponsResponseDto {
    private Long id;

    private String code;

    private DiscountType discountType;

    private Long discountValue;

    private LocalDate expirationDate;

    private boolean isValid;

    private boolean isUsed;

    private Timestamp creationTime;

    private Timestamp lastModifiedTime;

    public static UserCouponsResponseDto of(Long id, String code, DiscountType discountType, Long discountValue, LocalDate expirationDate, boolean isValid, boolean isUsed,  Timestamp creationTime, Timestamp lastModifiedTime){
        return new UserCouponsResponseDto(id, code, discountType, discountValue, expirationDate, isValid, isUsed, creationTime, lastModifiedTime);
    }
}

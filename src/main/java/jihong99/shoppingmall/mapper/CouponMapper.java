package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.entity.Coupon;

public class CouponMapper {
    public Coupon mapToCoupon(CouponRequestDto couponRequestDto){
        return Coupon.builder()
                .name(couponRequestDto.getName())
                .content(couponRequestDto.getContent())
                .expirationDate(couponRequestDto.getExpirationDate())
                .build();
    }

}

package jihong99.shoppingmall.service;


import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.dto.CouponResponseDto;
import jihong99.shoppingmall.entity.enums.Tiers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ICouponService {
    Page<CouponResponseDto> getUserCoupons(Long id, Pageable pageable);

    CouponResponseDto createCoupon(CouponRequestDto couponRequestDto);

    Page<CouponResponseDto> getAllCoupons(Pageable pageable);
    void distributeCouponToUser(Long couponId, Long UserId);
    void distributeCouponToUsersByTier(Long couponId, Tiers tier);
    void distributeCouponToAllUsers(Long couponId);

}

package jihong99.shoppingmall.service;


import jihong99.shoppingmall.dto.request.CouponRequestDto;
import jihong99.shoppingmall.dto.request.PatchCouponRequestDto;
import jihong99.shoppingmall.dto.response.CouponResponseDto;
import jihong99.shoppingmall.dto.response.UserCouponsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ICouponService {
    Page<UserCouponsResponseDto> getUserCoupons(Long userId, Pageable pageable);

    void createCoupon(CouponRequestDto couponRequestDto);

    Page<CouponResponseDto> getAllCoupons(Pageable pageable);
    void distributeCouponToUser(Long couponId, Long UserId);
    void distributeCouponToAllUsers(Long couponId);

    void deleteCoupon(Long couponId);
    void patchCoupon(Long couponId, PatchCouponRequestDto patchCouponRequestDto);

}

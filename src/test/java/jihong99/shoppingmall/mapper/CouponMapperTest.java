package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.entity.Coupon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


class CouponMapperTest {

    @Test
    void mapToCoupon() {
        // given
        CouponRequestDto couponRequestDto = new CouponRequestDto("Test Coupon", "Test Content", LocalDate.now().plusDays(10));
        // when
        CouponMapper couponMapper = new CouponMapper();
        Coupon coupon = couponMapper.mapToCoupon(couponRequestDto);
        // then
        assertThat(coupon.getName()).isEqualTo("Test Coupon");
        assertThat(coupon.getContent()).isEqualTo("Test Content");
        assertThat(coupon.getExpirationDate()).isEqualTo(LocalDate.now().plusDays(10));
    }
}
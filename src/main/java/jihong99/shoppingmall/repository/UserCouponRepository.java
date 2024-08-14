package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    void deleteAllByCouponId(Long couponId);
}

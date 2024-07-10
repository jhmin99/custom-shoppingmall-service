package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Page<UserCoupon> findByUsersId(Long userId, Pageable pageable);
    List<UserCoupon> findByUsersId(Long userId);
    Optional<UserCoupon> findByUsersIdAndCouponId(Long userId, Long CouponId);
}

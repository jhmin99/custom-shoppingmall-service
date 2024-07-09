package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Page<UserCoupon> findByUsersId(Long userId, Pageable pageable);
}

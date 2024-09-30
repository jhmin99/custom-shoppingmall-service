package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    void deleteAllByCouponId(Long couponId);

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.users.id = :userId " +
            "AND ( " +
            "(:status = 'available' AND uc.isValid = true AND uc.isUsed = false) OR " +
            "(:status = 'used' AND uc.isUsed = true) OR " +
            "(:status = 'invalid' AND uc.isValid = false) " +
            ")")
    Page<UserCoupon> findAllByUsersIdAndStatus(Long userId, String status, Pageable pageable);

    Optional<UserCoupon> findByUsersIdAndCouponId(Long userId, Long couponId);
}

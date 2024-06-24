package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon extends BaseEntity {

    // 회원 쿠폰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    // 회원 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    // 쿠폰 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    // 유효 여부
    private Boolean isValid;

}

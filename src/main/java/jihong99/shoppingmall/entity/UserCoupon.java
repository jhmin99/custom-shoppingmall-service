package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCoupon extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Boolean isValid;

    public void updateToInvalid(){
        this.isValid = false;
    }

    @Builder
    public UserCoupon(Users user, Coupon coupon, Boolean isValid){
        this.users = user;
        this.coupon = coupon;
        this.isValid = true;
    }

    public static UserCoupon createUserCoupon(Users user, Coupon coupon){
        return UserCoupon.builder()
                .user(user)
                .coupon(coupon)
                .isValid(true)
                .build();
    }
}

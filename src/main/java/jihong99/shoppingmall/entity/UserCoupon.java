package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents the relationship between a user and a coupon in the shopping mall system.
 *
 * <p>The UserCoupon entity stores information about the association between a user and a coupon,
 * including the validity and usage status of the coupon.</p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class UserCoupon extends BaseEntity {

    /**
     * Unique identifier for the user-coupon relationship.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    /**
     * The user associated with the coupon.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * The coupon associated with the user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /**
     * Indicates if the coupon is valid.
     */
    @Column(name = "is_valid")
    private Boolean isValid;

    /**
     * Indicates if the coupon has been used.
     */
    @Column(name = "is_used")
    private Boolean isUsed;

    /**
     * Sets the coupon status to invalid.
     */
    public void updateToInvalid(){
        this.isValid = false;
    }

    /**
     * Creates a new UserCoupon with the provided user and coupon.
     *
     * @param user The user associated with the coupon
     * @param coupon The coupon associated with the user
     * @return A new UserCoupon instance
     */
    public static UserCoupon createUserCoupon(Users user, Coupon coupon){
        return UserCoupon.builder()
                .users(user)
                .coupon(coupon)
                .isValid(true)
                .build();
    }
}

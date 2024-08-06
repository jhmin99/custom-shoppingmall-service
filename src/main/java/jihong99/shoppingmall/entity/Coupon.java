package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Represents a coupon that can be applied to a purchase for a discount.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Coupon extends BaseEntity {

    /**
     * Unique identifier for the coupon.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    /**
     * Code of the coupon.
     */
    private String code;

    /**
     * Type of discount the coupon provides (e.g., percentage or fixed amount).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    private DiscountType discountType;

    /**
     * Value of the discount provided by the coupon.
     */
    @Column(name = "discount_value")
    private Long discountValue;

    /**
     * Expiration date of the coupon.
     */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /**
     * Updates the discount type of the coupon.
     *
     * @param discountType the new discount type
     */
    public void updateDiscountType(DiscountType discountType){
        this.discountType = discountType;
    }

    /**
     * Updates the discount value of the coupon.
     *
     * @param discountValue the new discount value
     */
    public void updateDiscountValue(Long discountValue){
        this.discountValue = discountValue;
    }
}

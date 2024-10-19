package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
    @Column(unique = true)
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
     * Factory method to create a new Coupon instance.
     *
     * <p>This method generates a new coupon code based on the current date and a random UUID.
     * It sets the discount type, discount value, and expiration date for the coupon.</p>
     *
     * @param discountType The type of discount the coupon provides.
     * @param discountValue The value of the discount.
     * @param expirationDate The expiration date of the coupon.
     * @return A new instance of the Coupon entity.
     */
    public static Coupon of(DiscountType discountType, Long discountValue, LocalDate expirationDate) {
        String code = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + UUID.randomUUID().toString().substring(0, 8);
        return Coupon.builder()
                .code(code)
                .discountType(discountType)
                .discountValue(discountValue)
                .expirationDate(expirationDate)
                .build();
    }

    /**
     * Updates the discount type of the coupon.
     *
     * @param discountType The new discount type to be applied to the coupon.
     */
    public void updateDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    /**
     * Updates the discount value of the coupon.
     *
     * @param discountValue The new discount value to be applied to the coupon.
     */
    public void updateDiscountValue(Long discountValue) {
        this.discountValue = discountValue;
    }

    /**
     * Updates the expiration date of the coupon.
     *
     * @param expirationDate The new expiration date to be applied to the coupon.
     */
    public void updateExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }
}

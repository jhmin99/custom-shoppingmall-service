package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents the details of an order in the shopping mall system.
 *
 * <p>The OrderDetails entity stores information about the specific details of an order,
 * including the delivery address, applied coupon, total amount, and discount amount.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderDetails {

    /**
     * Unique identifier for the order detail.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    /**
     * The order associated with this detail.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    /**
     * The delivery address associated with this order detail.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliver_address_id")
    private DeliveryAddress deliveryAddress;

    /**
     * The coupon applied to this order detail.
     */
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon;

    /**
     * The total amount for this order detail before any discounts.
     */
    private Long total_amount;

    /**
     * The discount amount applied to this order detail.
     */
    private Long discount_amount;

    /**
     * Creates a new OrderDetails instance with the provided delivery address, applied coupon, total amount, and discount amount.
     *
     * @param deliveryAddress The delivery address for the order.
     * @param appliedCoupon   The coupon applied to the order.
     * @param totalAmount     The total amount before discounts.
     * @param discountAmount  The discount amount applied.
     * @return A new OrderDetails instance.
     */
    public static OrderDetails of(DeliveryAddress deliveryAddress, Coupon appliedCoupon, Long totalAmount, Long discountAmount) {
        return OrderDetails.builder()
                .deliveryAddress(deliveryAddress)
                .appliedCoupon(appliedCoupon)
                .total_amount(totalAmount)
                .discount_amount(discountAmount)
                .build();
    }

    /**
     * Calculates the final amount for this order detail after applying the discount.
     *
     * @return The final amount after the discount.
     */
    public Long calculateFinalAmount() {
        return this.total_amount - this.discount_amount;
    }
}

package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.DiscountType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shopping cart.
 *
 * <p>The Cart entity stores information about a user's shopping cart,
 * including the estimated total price of items in the cart and any applied coupon.</p>
 */
@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Cart extends BaseEntity {

    /**
     * Unique identifier for the cart.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @Column(name = "original_total_price")
    private Long originalTotalPrice = 0L;

    /**
     * Estimated total price of items in the cart.
     * Default value: 0.
     */
    @Column(name = "estimated_total_price")
    private Long estimatedTotalPrice = 0L;

    /**
     * Coupon applied to the cart, if any.
     */
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Static factory method to create a new cart instance.
     *
     * @return a new Cart instance
     */
    public static Cart of() {
        return Cart.builder()
                .build();
    }

    /**
     * Updates the applied coupon for the cart.
     *
     * @param coupon the coupon to be applied
     */
    public void updateAppliedCoupon(Coupon coupon) {
        this.appliedCoupon = coupon;
    }

    public void recalculateTotalPrices() {
        this.originalTotalPrice = cartItems.stream()
                .mapToLong(cartItem -> cartItem.getPrice() * cartItem.getQuantity())
                .sum();
        this.estimatedTotalPrice = this.originalTotalPrice;

        applyCouponDiscount();
    }

    private void applyCouponDiscount() {
        if (this.appliedCoupon != null) {
            if (this.appliedCoupon.getDiscountType() == DiscountType.PERCENTAGE) {
                this.estimatedTotalPrice -= (this.originalTotalPrice * this.appliedCoupon.getDiscountValue()) / 100;
            } else if (this.appliedCoupon.getDiscountType() == DiscountType.FIXED) {
                this.estimatedTotalPrice -= this.appliedCoupon.getDiscountValue();
            }
            if (this.estimatedTotalPrice < 0) {
                this.estimatedTotalPrice = 0L;
            }
        }
    }

    public void addCartItem(CartItem cartItem){
        this.cartItems.add(cartItem);
        cartItem.setCart(this);
    }
    public void removeCartItem(CartItem cartItem){
        this.cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

}

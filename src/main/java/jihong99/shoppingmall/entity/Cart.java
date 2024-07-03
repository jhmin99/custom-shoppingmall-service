package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {

    /**
     * Represents a shopping cart.
     *
     * <p>The Cart entity stores information about a user's shopping cart,
     * including the estimated total price of items in the cart.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    /**
     * Estimated total price of items in the cart.
     * Default value: 0
     */
    private Long estimatedTotalPrice;

    /**
     * Constructs a new cart with the provided estimated total price.
     *
     * @param estimatedTotalPrice The estimated total price of items in the cart
     */

    @Builder
    public Cart(Long estimatedTotalPrice){
        this.estimatedTotalPrice = estimatedTotalPrice;
    }
    public static Cart createCart(Long estimatedTotalPrice) {
        return Cart.builder()
                .estimatedTotalPrice(estimatedTotalPrice)
                .build();
    }

}

package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents an item in an order in the shopping mall system.
 *
 * <p>The OrderItem entity stores information about a specific item within an order,
 * including the quantity and total price of the item.</p>
 */
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderItem extends BaseEntity {

    /**
     * Unique identifier for the order item.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    /**
     * The order associated with this order item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    /**
     * The item associated with this order item.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * The quantity of the item ordered.
     */
    private Integer quantity;

    /**
     * The total price for this order item.
     */
    @Column(name = "total_price")
    private Long totalPrice;
}

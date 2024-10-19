package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.OrderStatus;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Represents an order in the shopping mall system.
 *
 * <p>The Orders entity stores information about a user's order,
 * including the order number, order date, status, and the final amount.</p>
 */
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Orders extends BaseEntity {

    /**
     * Unique identifier for the order.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    /**
     * Unique order number for the order.
     */
    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    /**
     * The user who placed the order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * The date when the order was placed.
     */
    @Column(name = "order_date")
    private LocalDate orderDate;

    /**
     * The status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    /**
     * The final amount of the order after applying discounts and other adjustments.
     */
    @Column(name = "final_amount")
    private Long finalAmount;

    /**
     * Creates a new order with the provided details.
     *
     * @param orderNumber The unique order number
     * @param users The user who placed the order
     * @param orderDate The date when the order was placed
     * @param orderStatus The status of the order
     * @param finalAmount The final amount of the order
     * @return A new Orders instance
     */
    public static Orders of(String orderNumber, Users users, LocalDate orderDate, OrderStatus orderStatus, Long finalAmount) {
        return Orders.builder()
                .orderNumber(orderNumber)
                .users(users)
                .orderDate(orderDate)
                .orderStatus(orderStatus)
                .finalAmount(finalAmount)
                .build();
    }

    /**
     * Updates the status of the order.
     *
     * @param orderStatus The new status of the order
     */
    public void updateOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}

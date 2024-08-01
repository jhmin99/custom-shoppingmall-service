package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents an alert for a specific item.
 *
 * <p>The ItemAlert entity stores information about alerts set by users for specific items.
 * It includes the user who set the alert and the item for which the alert is set.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ItemAlert extends BaseEntity {

    /**
     * Unique identifier for the item alert.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_alert_id")
    private Long id;

    /**
     * User who set the alert.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * Item for which the alert is set.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

}

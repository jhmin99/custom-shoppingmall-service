package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents an item in a user's wish list.
 *
 * <p>The WishItem entity stores information about an item that a user has added to their wish list.</p>
 */
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WishItem extends BaseEntity {

    /**
     * Primary key for the wish item entity.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_item_id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * The wish list that this wish item belongs to.
     * It is a foreign key referencing the wish list entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;


    public static WishItem createWishItem(Item item, WishList wishList){
        return WishItem.builder()
                .item(item)
                .wishList(wishList)
                .build();
    }
    public void setWishList(WishList wishList){
        this.wishList = wishList;
    }
}

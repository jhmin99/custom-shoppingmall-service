package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's wish list.
 *
 * <p>The WishList entity stores information about a user's wish list.</p>
 */
@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class WishList extends BaseEntity {

    /**
     * Primary key for the wish list entity.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_list_id")
    private Long id;


    @OneToMany(mappedBy = "wishList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishItem> wishItems = new ArrayList<>();

    /**
     * Static factory method to create a new wish list.
     *
     * @return A new WishList instance
     */
    public static WishList createWishList() {
        return WishList.builder()
                .build();
    }

    public void addWishItem(WishItem wishItem) {
        wishItems.add(wishItem);
        wishItem.setWishList(this);
    }

    public void removeWishItem(WishItem wishItem) {
        wishItems.remove(wishItem);
        wishItem.setWishList(null);
    }
}

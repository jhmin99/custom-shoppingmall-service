package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

/**
 * Represents a user's wish list.
 *
 * <p>The WishList entity stores information about a user's wish list.</p>
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WishList extends BaseEntity {

    /**
     * Primary key for the wish list entity.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_list_id")
    private Long id;

    /**
     * Constructs a new WishList with the provided id.
     *
     * @param id The id of the wish list
     */
    @Builder
    public WishList(Long id) {
        this.id = id;
    }

    /**
     * Static factory method to create a new wish list.
     *
     * @return A new WishList instance
     */
    public static WishList createWishList() {
        return WishList.builder()
                .build();
    }
}

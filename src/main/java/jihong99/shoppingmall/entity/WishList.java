package jihong99.shoppingmall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class WishList extends BaseEntity {

    /**
     * Primary key for the wish list entity
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

}

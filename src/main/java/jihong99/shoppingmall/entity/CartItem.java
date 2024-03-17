package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {

    // 장바구니 상품 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    // 상품 번호 (fk)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", unique = true)
    private Item item;

    // 장바구니 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * 수량
     * default : 1
     */
    private Integer quantity;

    // 가격
    private Long price;

}

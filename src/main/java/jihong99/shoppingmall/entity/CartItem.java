package jihong99.shoppingmall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {

    // 장바구니 상품 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    /**
     * 수량
     * default : 1
     */
    private Integer quantity;

    // 가격
    @NotNull
    private Long price;

}

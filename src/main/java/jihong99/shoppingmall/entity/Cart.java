package jihong99.shoppingmall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    // 장바구니 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    /**
     * 예상 총 금액
     * default : 0
     */
    private Long estimatedTotalPrice;

}

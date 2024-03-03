package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    // 장바구니 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    // 카테고리 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 예상 총 금액
     * default : 0
     */
    private Long estimatedTotalPrice;


}

package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {


    // 주문 상품 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    // 주문 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orders;

    // 상품 번호 (fk)
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    // 수량
    private Integer quantity;

    // 가격
    private Long price;
}

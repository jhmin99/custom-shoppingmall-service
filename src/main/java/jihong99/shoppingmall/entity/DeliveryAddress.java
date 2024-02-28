package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress extends BaseEntity {

    // 배송지 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryAddressId;

    // 회원 번호
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 수령인 이름
    private String name;

    // 수령인 핸드폰 번호
    private String phoneNumber;

    // 수령인 주소
    private String address;

    // 수령인 주소 상세
    private String addressDetail;

}
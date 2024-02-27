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
public class DeliveryAddress extends BaseEntity {

    // 배송지 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryAddressId;

    // 수령인 이름
    @NotNull
    private String name;

    // 수령인 핸드폰 번호
    @NotNull
    private String phoneNumber;

    // 수령인 주소
    @NotNull
    private String address;

    // 수령인 주소 상세
    private String addressDetail;

}

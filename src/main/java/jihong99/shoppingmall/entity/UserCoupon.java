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
public class UserCoupon extends BaseEntity {

    // 회원 쿠폰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    // 유효 여부
    @NotNull
    private Boolean isValid;

}

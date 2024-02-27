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

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon extends BaseEntity {

    // 쿠폰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    // 쿠폰 이름
    @NotNull
    private String name;

    // 쿠폰 내용
    @NotNull
    private String content;

    // 만료 날짜
    @NotNull
    private LocalDate expirationDate;
}

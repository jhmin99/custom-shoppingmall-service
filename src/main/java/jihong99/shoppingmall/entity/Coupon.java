package jihong99.shoppingmall.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon extends BaseEntity {

    // 쿠폰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    // 쿠폰 이름
    private String name;

    // 쿠폰 내용
    private String content;

    // 만료 날짜
    private LocalDate expirationDate;
}

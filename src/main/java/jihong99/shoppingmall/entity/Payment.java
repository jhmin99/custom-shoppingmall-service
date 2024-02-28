package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.PaymentMethod;
import jihong99.shoppingmall.entity.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment extends BaseEntity {

    // 결제 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    // 회원 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 결제 수단
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    // 결제 금액
    private Long amount;

    // 결제 상태
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // 결제 일자
    @CreatedDate
    @Column(updatable = false)
    private LocalDate payment_date;

}

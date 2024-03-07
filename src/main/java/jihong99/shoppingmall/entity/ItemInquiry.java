package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ItemInquiry extends BaseEntity {

    // 상품문의 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemInquiryId;

    // 회원 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 상품 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 상품문의 제목
    private String title;

    // 상품문의 내용
    private String content;

    // 공개/비공개 상태
    @Enumerated(EnumType.STRING)
    private InquiryStatus state;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;


}

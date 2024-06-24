package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Image extends BaseEntity {

    // 이미지 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    // 상품 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 리뷰 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    // 이벤트 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // 상품문의 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_inquiry_id")
    private ItemInquiry itemInquiry;

    // 이미지 이름
    private String name;

    // 이미지 경로
    private String url;

    // 이미지 설명
    private String description;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;

}

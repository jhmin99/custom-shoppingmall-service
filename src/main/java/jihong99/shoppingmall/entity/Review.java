package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
public class Review extends BaseEntity {

    // 리뷰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    // 회원 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 상품 번호 (fk)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 별점
    private Integer rating;

    // 리뷰 제목
    private String title;

    // 리뷰 내용
    @NotNull
    private String content;

    /**
     * 글자 수
     * default : 0
     */
    private Integer characterCount;

    // 사진 첨부 여부
    private Boolean hasPhoto;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;
}

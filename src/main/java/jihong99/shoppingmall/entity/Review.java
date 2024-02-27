package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review extends BaseEntity {

    // 리뷰 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    // 별점
    @NotNull
    @Min(value = 0 , message = "별점은 0보다 작을 수 없습니다")
    @Max(value = 5, message = "별점은 5보다 클 수 없습니다")
    private Integer rating;

    // 리뷰 제목
    @NotNull
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
    @NotNull
    private Boolean hasPhoto;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;
}

package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.InquiryStatus;
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
public class ItemInquiry extends BaseEntity {

    // 상품문의 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemInquiryId;

    // 상품문의 제목
    @NotNull
    private String title;

    // 상품문의 내용
    @NotNull
    private String content;

    // 공개/비공개 상태
    @NotNull
    @Enumerated(EnumType.STRING)
    private InquiryStatus state;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;


}

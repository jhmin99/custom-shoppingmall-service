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
public class Item extends BaseEntity {

    // 상품 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    // 카테고리 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 상품 이름
    private String name;

    // 상품 가격
    private Integer price;

    // 재고
    private Integer inventory;

    // 키워드
    private String keyword;

    // 등록 날짜
    @CreatedDate
    private LocalDate registrationDate;

}

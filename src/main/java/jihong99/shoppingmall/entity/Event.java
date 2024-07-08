package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Event extends BaseEntity {

    // 이벤트 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    // 이벤트 제목
    private String title;

    // 이벤트 내용
    private String content;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;

}

package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alarm extends BaseEntity {

    // 알림 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    // 알림 제목
    private String title;

    // 알림 내용
    private String content;

    // 수신자
    private String receiver;

    // 발신자
    private String sender;


}

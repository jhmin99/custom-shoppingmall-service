package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarm extends BaseEntity {

    // 회원 알림 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAlarmId;

    // 회원 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 알림 번호 (fk)
    @ManyToOne
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;


}
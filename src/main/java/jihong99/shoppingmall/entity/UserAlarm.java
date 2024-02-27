package jihong99.shoppingmall.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAlarm extends BaseEntity {

    // 회원 알림 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAlarmId;

}

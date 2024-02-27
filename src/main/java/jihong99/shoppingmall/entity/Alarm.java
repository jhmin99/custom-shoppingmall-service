package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Alarm extends BaseEntity {

    // 알림 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    // 알림 제목
    @NotNull
    private String title;

    // 알림 내용
    @NotNull
    private String content;

    // 수신자
    @NotNull
    private String receiver;

    // 발신자
    @NotNull
    private String sender;


}

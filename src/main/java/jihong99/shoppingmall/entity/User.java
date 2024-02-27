package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.Tier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseEntity {

    // 회원 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * 아이디
     * regex :
     * 영문, 숫자를 모두 포함하는 6-10자리 문자열
     * 영문, 숫자로만 이루어짐
     */
    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,10}$")
    @Size(min = 6, max = 10)
    private String id;

    /**
     * 비밀번호
     * regex :
     * 영문, 숫자, 특수문자를 모두 포함하는 8-15자리 문자열
     * 특수문자는 !@#$%^&*만 유효
     */
    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,15}$")
    @Size(min = 8, max = 15)
    private String password;

    // 이름
    @NotNull
    private String name;

    // 생년월일
    @NotNull
    private LocalDate birthDate;

    // 핸드폰 번호
    @NotNull
    private String phoneNumber;

    /**
     * 포인트
     * default : 0
     */
    private Integer point;

    /**
     * 등급
     * default : IRON
     */
    @Enumerated(EnumType.STRING)
    private Tier tier;

    /**
     * 다음 등급까지 남은 금액
     * default : 50000
     */
    private Integer amountToNextTier;

    // 등록 날짜
    @CreatedDate
    @Column(updatable = false)
    private LocalDate registrationDate;


}

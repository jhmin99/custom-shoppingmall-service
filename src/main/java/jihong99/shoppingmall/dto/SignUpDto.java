package jihong99.shoppingmall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignUpDto {

    /**
     * 아이디
     * regex :
     * 영문, 숫자를 모두 포함하는 6-10자리 문자열
     * 영문, 숫자로만 이루어짐
     */
    @NotNull
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,10}$")
    @Size(min = 6, max = 10)
    private String identification;

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

    // 비밀번호와 일치하는지 확인
    @NotNull
    private String confirmPassword;

    // 이름
    @NotNull
    private String name;

    // 생년월일
    @NotNull
    private LocalDate birthDate;

    // 핸드폰 번호
    @NotNull
    private String phoneNumber;


}

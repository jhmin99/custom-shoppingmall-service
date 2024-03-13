package jihong99.shoppingmall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class SignUpDto {

    /**
     * 아이디
     * regex :
     * 영문, 숫자를 모두 포함하는 6-10자리 문자열
     * 영문, 숫자로만 이루어짐
     */
    @NotNull(message = "아이디는 필수 항목입니다.", groups = {IdentificationValidation.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,10}$", message = "아이디는 영문, 숫자로만 이루어지며, 영문, 숫자를 모두 포함하는 6-10자리 문자열입니다.")
    @Size(min = 6, max = 10, message = "아이디는 6-10자리 문자열 입니다.")
    private String identification;

    /**
     * 비밀번호
     * regex :
     * 영문, 숫자, 특수문자를 모두 포함하는 8-15자리 문자열
     * 특수문자는 !@#$%^&*만 유효
     */
    @NotNull(message = "비밀번호는 필수 항목입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,15}$", message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함하는 8-15자리 문자열입니다. ( 특수문자는 !@#$%^&*만 유효 )")
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자리 문자열 입니다.")
    private String password;

    // 비밀번호와 일치하는지 확인
    @NotNull(message = "비밀번호 확인은 필수 항목입니다.")
    private String confirmPassword;

    // 이름
    @NotNull(message = "이름은 필수 항목입니다.")
    private String name;

    // 생년월일
    @NotNull(message = "생년월일은 필수 항목입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 xxxx-xx-xx 형식이어야 합니다.")
    private String birthDate;

    // 핸드폰 번호
    @NotNull(message = "핸드폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "\\d{11}", message = "핸드폰번호는 숫자로만 이루어진 11자리 문자열이어야 합니다.")
    private String phoneNumber;
}

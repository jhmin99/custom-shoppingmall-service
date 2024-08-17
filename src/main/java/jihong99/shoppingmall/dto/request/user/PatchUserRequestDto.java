package jihong99.shoppingmall.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserRequestDto {
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
    private String name;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birth Date must be in the format yyyy-mm-dd.")
    private String birthDate;

    @Pattern(regexp = "\\d{11}", message = "Phone Number must consist of 11 digits.")
    private String phoneNumber;

}

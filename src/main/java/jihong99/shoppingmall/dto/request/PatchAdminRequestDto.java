package jihong99.shoppingmall.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchAdminRequestDto {

    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
    private String name;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,15}$",
            message = "Password must contain alphabets, numbers, and special characters, with a length of 8-15 characters. (Allowed special characters: !@#$%^&*)")
    private String password;

    @Pattern(regexp = "\\d{11}", message = "Phone Number must consist of 11 digits.", groups = {SignUpValidation.class})
    private String phoneNumber;

    @Pattern(regexp = "USER|ADMIN|SUPER_ADMIN", message = "Role must be one of the following: USER, ADMIN, SUPER_ADMIN")
    private String role;
}

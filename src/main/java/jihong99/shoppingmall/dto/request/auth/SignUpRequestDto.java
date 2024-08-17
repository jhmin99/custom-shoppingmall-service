package jihong99.shoppingmall.dto.request.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jihong99.shoppingmall.validation.groups.IdentificationValidation;
import jihong99.shoppingmall.validation.groups.SignUpValidation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    /**
     * ID
     * regex:
     * A 6-10 character string containing both alphabets and numbers
     * Consists only of alphabets and numbers
     */
    @NotNull(message = "ID is a required field.", groups = {IdentificationValidation.class, SignUpValidation.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,10}$", message = "ID must consist of alphabets and numbers, containing both, with a length of 6-10 characters.", groups = {IdentificationValidation.class, SignUpValidation.class})
    private String identification;

    /**
     * Password
     * regex:
     * An 8-15 character string containing alphabets, numbers, and special characters
     * Special characters allowed: !@#$%^&*
     */
    @NotNull(message = "Password is a required field.", groups = {SignUpValidation.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,15}$", message = "Password must contain alphabets, numbers, and special characters, with a length of 8-15 characters. (Allowed special characters: !@#$%^&*)", groups = {SignUpValidation.class})
    private String password;

    /**
     * Confirm Password
     */
    @NotNull(message = "Confirm Password is a required field.", groups = {SignUpValidation.class})
    private String confirmPassword;

    /**
     * Name
     */
    @NotNull(message = "Name is a required field.", groups = {SignUpValidation.class})
    private String name;

    /**
     * Birth Date
     * Format: yyyy-mm-dd
     */
    @NotNull(message = "Birth Date is a required field.", groups = {SignUpValidation.class})
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birth Date must be in the format yyyy-mm-dd.", groups = {SignUpValidation.class})
    private String birthDate;

    /**
     * Phone Number
     * regex:
     * 11-digit string consisting only of numbers
     */
    @NotNull(message = "Phone Number is a required field.", groups = {SignUpValidation.class})
    @Pattern(regexp = "\\d{11}", message = "Phone Number must consist of 11 digits.", groups = {SignUpValidation.class})
    private String phoneNumber;
}

package jihong99.shoppingmall.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class CouponRequestDto {
    @NotNull(message = "Name is a required field.")
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
    private String name;
    @NotNull(message = "Content is a required field.")
    @Size(max = 100, message = "Content must be less than 100 characters.")
    private String content;
    @NotNull(message = "ExpirationDate is a required field.")
    private LocalDate expirationDate;
}

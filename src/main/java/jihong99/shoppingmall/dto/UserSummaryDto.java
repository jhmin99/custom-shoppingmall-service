package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserSummaryDto {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String deliveryAddress;
    private LocalDate registrationDate;
}

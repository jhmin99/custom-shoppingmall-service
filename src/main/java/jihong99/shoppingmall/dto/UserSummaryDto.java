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
    private Long addressId;
    private Integer zipCode;
    private String address;
    private String addressDetail;
    private LocalDate registrationDate;
}

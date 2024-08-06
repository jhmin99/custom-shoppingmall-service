package jihong99.shoppingmall.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserSummaryResponseDto {
    private Long id;
    private String identification;
    private String name;
    private LocalDate birthDate;
    private List<Address> addresses;
    private String phoneNumber;
    private Timestamp creationTime;
    private Timestamp lastModifiedTime;

    private class Address {
        private Long addressId;
        private Integer zipCode;
        private String address;
        private String addressDetail;
    }
}

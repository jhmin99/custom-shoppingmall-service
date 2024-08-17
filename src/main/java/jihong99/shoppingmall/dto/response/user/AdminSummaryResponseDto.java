package jihong99.shoppingmall.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AdminSummaryResponseDto {
    private Long id;
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Timestamp creationTime;
    private Timestamp lastModifiedTime;

    public static AdminSummaryResponseDto of(Long id, String identification, String name, LocalDate birthDate, String phoneNumber, Timestamp creationTime, Timestamp lastModifiedTime){
        return new AdminSummaryResponseDto(id, identification, name, birthDate, phoneNumber, creationTime, lastModifiedTime);
    }
}

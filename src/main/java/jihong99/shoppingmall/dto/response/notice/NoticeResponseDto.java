package jihong99.shoppingmall.dto.response.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class NoticeResponseDto {
    Long id;
    String title;
    LocalDate registrationDate;

    public static NoticeResponseDto of(Long id, String title, LocalDate registrationDate){
        return new NoticeResponseDto(id, title, registrationDate);
    }
}

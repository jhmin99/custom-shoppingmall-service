package jihong99.shoppingmall.dto.response.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class NoticeDetailsResponseDto {
    String title;
    String content;
    LocalDate registrationDate;

    public static NoticeDetailsResponseDto of(String title, String content, LocalDate registrationDate){
        return new NoticeDetailsResponseDto(title, content, registrationDate);
    }
}

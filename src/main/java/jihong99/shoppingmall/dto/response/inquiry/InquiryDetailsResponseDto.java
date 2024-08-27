package jihong99.shoppingmall.dto.response.inquiry;

import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class InquiryDetailsResponseDto {
    private Long id;
    private InquiryType type;
    private String itemName;
    private String title;
    private String content;
    private InquiryStatus status;
    private LocalDate registrationDate;
    private List<ResponseDto> responses;

    @Getter
    @AllArgsConstructor
    public static class ResponseDto {
        private Long id;
        private String content;
        private LocalDate registrationDate;
        private List<ResponseDto> childResponses;

        public static ResponseDto of(Long id, String content, LocalDate registrationDate, List<ResponseDto> childResponses) {
            return new ResponseDto(id, content, registrationDate, childResponses);
        }
    }

    public static InquiryDetailsResponseDto of(Long id, InquiryType type, String itemName, String title, String content, InquiryStatus status, LocalDate registrationDate, List<ResponseDto> responses) {
        return new InquiryDetailsResponseDto(id, type, itemName, title, content, status, registrationDate, responses);
    }
}

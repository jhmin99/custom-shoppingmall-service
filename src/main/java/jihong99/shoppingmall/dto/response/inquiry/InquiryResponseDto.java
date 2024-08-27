package jihong99.shoppingmall.dto.response.inquiry;

import jihong99.shoppingmall.entity.enums.InquiryStatus;
import jihong99.shoppingmall.entity.enums.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InquiryResponseDto {
    private Long id;
    private String itemName;
    private String title;
    private InquiryType type;
    private InquiryStatus status;



    public static InquiryResponseDto of(Long id, String itemName, String title, InquiryType type, InquiryStatus status){
        return new InquiryResponseDto(id, itemName, title, type, status);
    }
}

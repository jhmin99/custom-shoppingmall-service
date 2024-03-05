package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDto {

    // 상태 코드
    private String statusCode;

    // 상태 메세지
    private String statusMessage;
}

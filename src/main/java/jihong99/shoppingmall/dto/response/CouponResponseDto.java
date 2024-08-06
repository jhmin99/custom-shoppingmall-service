package jihong99.shoppingmall.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
@Getter
@AllArgsConstructor
public class CouponResponseDto {
    private Long id;

    private String name;

    private String content;

    private LocalDate expirationDate;

    public static CouponResponseDto of(Long id, String name, String content, LocalDate expirationDate){
        return new CouponResponseDto(id, name, content, expirationDate);
    }
}

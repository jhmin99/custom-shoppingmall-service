package jihong99.shoppingmall.dto.request.notice;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchNoticeRequestDto {
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    private String title;
    @Size(min = 5, max = 500, message = "Content must be between 5 and 500 characters.")
    private String content;
}

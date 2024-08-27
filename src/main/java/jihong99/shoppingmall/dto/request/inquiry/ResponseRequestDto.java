package jihong99.shoppingmall.dto.request.inquiry;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseRequestDto {

    @NotNull(message = "Content is a required field.")
    @Size(min = 5, max = 500, message = "Content must be between 5 and 500 characters.")
    private String content;
}

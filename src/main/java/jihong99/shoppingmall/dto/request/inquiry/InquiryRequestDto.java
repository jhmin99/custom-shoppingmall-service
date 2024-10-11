package jihong99.shoppingmall.dto.request.inquiry;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InquiryRequestDto {
    /**
     * The title of the inquiry.
     * Must be between 3 and 100 characters.
     */
    @NotBlank(message = "Title is a required field.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    private String title;

    /**
     * The content of the inquiry.
     * Must be between 5 and 500 characters.
     */
    @NotBlank(message = "Content is a required field.")
    @Size(min = 5, max = 500, message = "Content must be between 5 and 500 characters.")
    private String content;
}

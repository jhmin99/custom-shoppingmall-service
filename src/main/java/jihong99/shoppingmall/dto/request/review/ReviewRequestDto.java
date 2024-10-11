package jihong99.shoppingmall.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {
    /**
     * The title of the review.
     * Must be between 3 and 100 characters.
     */
    @NotBlank(message = "Title is a required field.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    private String title;

    /**
     * The content of the review.
     * Must be between 5 and 500 characters.
     */
    @NotBlank(message = "Content is a required field.")
    @Size(min = 5, max = 500, message = "Content must be between 5 and 500 characters.")
    private String content;

    /**
     * The rating of the review.
     * Must be a number between 0 and 5.
     * 0 means no stars, and 1-5 indicates the star rating.
     */
    @Min(value = 0, message = "Rating must be at least 0.")
    @Max(value = 5, message = "Rating must be no more than 5.")
    private Integer rating;
}

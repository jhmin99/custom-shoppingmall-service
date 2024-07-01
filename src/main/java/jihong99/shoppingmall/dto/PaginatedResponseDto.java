package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private String statusCode;
    private String statusMessage;
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public static <T> PaginatedResponseDto<T> of(Page<T> page, String statusCode, String statusMessage) {
        return new PaginatedResponseDto<>(
                statusCode,
                statusMessage,
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
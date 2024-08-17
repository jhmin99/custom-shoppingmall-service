package jihong99.shoppingmall.dto.response.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaginatedResponseDto<T> {
    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public static <T> PaginatedResponseDto<T> of(Page<T> page) {
        return new PaginatedResponseDto<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
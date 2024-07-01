package jihong99.shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer inventory;
    private String keyword;
    private LocalDate registrationDate;
    private List<String> categoryNames;

    public static ItemResponseDto of(Long id, String name, Integer price, Integer inventory, String keyword, LocalDate registrationDate,
                                     List<String> categoryNames) {
        return new ItemResponseDto(id, name, price, inventory, keyword, registrationDate, categoryNames);
    }

}
package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
class ItemMapperTest {

    @Test
    void mapToItem() {
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(1L);
        categoryIds.add(2L);
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto("Sample Item", 100, 50, "#sample#sample2"
                , categoryIds);
        // when
        ItemMapper itemMapper = new ItemMapper();
        Item item = itemMapper.mapToItem(itemRequestDto);
        // then
        assertThat(item.getName()).isEqualTo("Sample Item");
        assertThat(item.getPrice()).isEqualTo(100);
        assertThat(item.getInventory()).isEqualTo(50);
        assertThat(item.getKeyword()).isEqualTo("#sample#sample2");
    }
}

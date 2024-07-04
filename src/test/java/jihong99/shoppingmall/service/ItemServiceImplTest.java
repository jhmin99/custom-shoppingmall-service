package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CategoryItemRepository;
import jihong99.shoppingmall.repository.CategoryRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryItemRepository categoryItemRepository;

    @Autowired
    private IItemService itemService;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        categoryItemRepository.deleteAll();
        categoryRepository.deleteAll();
        itemRepository.deleteAll();    }

    @Test
    @Transactional
    void createItem_Success() {
        // given
        Category category1 = new Category(null,"Category 1");
        Category category2 = new Category(null,"Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        ItemRequestDto itemRequestDto = new ItemRequestDto("Sample Item", 1000, 10, "#keyword", Arrays.asList(category1.getId(), category2.getId()));

        // when
        ItemResponseDto itemResponseDto = itemService.createItem(itemRequestDto);

        // then
        assertThat(itemResponseDto.getId()).isNotNull();
        assertThat(itemResponseDto.getName()).isEqualTo("Sample Item");
        assertThat(itemResponseDto.getPrice()).isEqualTo(1000);
        assertThat(itemResponseDto.getInventory()).isEqualTo(10);
        assertThat(itemResponseDto.getKeyword()).isEqualTo("#keyword");
        assertThat(itemResponseDto.getCategoryNames()).containsExactlyInAnyOrder("Category 1", "Category 2");
    }

    @Test
    void createItem_NotFoundException() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto("Sample Item", 1000, 10, "#keyword", Arrays.asList(1L, 2L));

        // when & then
        assertThrows(NotFoundException.class, () -> {
            itemService.createItem(itemRequestDto);
        });
    }
}

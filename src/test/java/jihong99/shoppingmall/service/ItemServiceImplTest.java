package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.entity.Item;
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

import static jihong99.shoppingmall.constants.Constants.MESSAGE_404_ItemNotFound;
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


    @Test
    @Transactional
    void updateItem_Success() {
        // given
        Category category1 = new Category(null,"Category 1");
        Category category2 = new Category(null,"Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        ItemRequestDto itemRequestDto = new ItemRequestDto("new Item", 1500, 20, "#new", Arrays.asList(category1.getId(), category2.getId()));
        Long itemId = itemService.createItem(itemRequestDto).getId();

        ItemRequestDto itemRequestDto2 = new ItemRequestDto("Updated Item", 1500, 20, "#updated", Arrays.asList(category1.getId(), category2.getId()));

        // when
        itemService.updateItem(itemId, itemRequestDto2);
        Item updatedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(MESSAGE_404_ItemNotFound));

        // then
        assertThat(updatedItem.getName()).isEqualTo("Updated Item");
        assertThat(updatedItem.getPrice()).isEqualTo(1500);
        assertThat(updatedItem.getInventory()).isEqualTo(20);
        assertThat(updatedItem.getKeyword()).isEqualTo("#updated");
    }

    @Test
    void updateItem_NotFoundException(){
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto("Updated Item", 1500, 20, "#updated", Arrays.asList(1L, 2L));

        // when & then
        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(999L, itemRequestDto);
        });
    }
    @Test
    void deleteItem_Success(){
        // given
        Category category1 = new Category(null,"Category 1");
        Category category2 = new Category(null,"Category 2");
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        ItemRequestDto itemRequestDto = new ItemRequestDto("new Item", 1500, 20, "#new", Arrays.asList(category1.getId(), category2.getId()));
        Long itemId = itemService.createItem(itemRequestDto).getId();
        // when
        itemService.deleteItem(itemId);
        // then
        assertThrows(NotFoundException.class, () -> {
            itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(MESSAGE_404_ItemNotFound));
        });
    }
    @Test
    void deleteItem_NotFoundException(){
        // when & then
        assertThrows(NotFoundException.class, () -> {
            itemService.deleteItem(-1L);
        });
    }




}

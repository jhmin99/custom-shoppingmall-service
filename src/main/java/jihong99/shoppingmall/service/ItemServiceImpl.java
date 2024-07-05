package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;
import jihong99.shoppingmall.entity.Category;
import jihong99.shoppingmall.entity.CategoryItem;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.mapper.CategoryItemMapper;
import jihong99.shoppingmall.mapper.ItemMapper;
import jihong99.shoppingmall.repository.CategoryItemRepository;
import jihong99.shoppingmall.repository.CategoryRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements IItemService{
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryItemRepository categoryItemRepository;

    /**
     * Creates a new item and associates it with categories.
     *
     * <p>This method maps the provided {@link ItemRequestDto} to an {@link Item} entity,
     * saves the item in the database, and then associates the item with the specified categories.
     * If a category ID provided in the request does not exist, a {@link NotFoundException} is thrown.</p>
     *
     * @param itemRequestDto the item request data transfer object containing the details of the item to be created
     * @return an {@link ItemResponseDto} containing the details of the created item and its associated categories
     * @throws NotFoundException if any of the specified category IDs do not exist
     */
    @Transactional
    @Override
    public ItemResponseDto createItem(ItemRequestDto itemRequestDto) {
        Item savedItem = createItemEntity(itemRequestDto);
        saveCategoryItems(itemRequestDto, savedItem);
        List<String> categoryNames = getCategoryNames(itemRequestDto);

        return ItemResponseDto.of(savedItem.getId(), savedItem.getName(), savedItem.getPrice(), savedItem.getInventory(),
                savedItem.getKeyword(), savedItem.getRegistrationDate(), categoryNames);
    }
    /**
     * Updates an existing item with the specified details.
     *
     * <p>This method finds the item by its ID, updates its fields with the values from the provided {@link ItemRequestDto},
     * and saves the updated item back to the database. If the item ID does not exist, a {@link NotFoundException} is thrown.</p>
     *
     * @param itemId the ID of the item to be updated
     * @param itemRequestDto the item request data transfer object containing the updated item details
     * @throws NotFoundException if the item with the specified ID does not exist
     */
    @Override
    @Transactional
    public void updateItem(Long itemId, ItemRequestDto itemRequestDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(MESSAGE_404_ItemNotFound));
        applyItemUpdates(itemRequestDto, item);
        itemRepository.save(item);
    }

    private void applyItemUpdates(ItemRequestDto itemRequestDto, Item item) {
        item.updateName(itemRequestDto.getName());
        item.updatePrice(itemRequestDto.getPrice());
        item.updateInventory(itemRequestDto.getInventory());
        item.updateKeyword(itemRequestDto.getKeyword());
    }
    /**
     * Deletes an existing item by its ID.
     *
     * <p>This method finds the item by its ID and deletes it from the database.
     * If the item ID does not exist, a {@link NotFoundException} is thrown.</p>
     *
     * @param itemId the ID of the item to be deleted
     * @throws NotFoundException if the item with the specified ID does not exist
     */
    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        List<CategoryItem> categoryItems = categoryItemRepository.findByItemId(itemId);
        if (!categoryItems.isEmpty()) {
            categoryItemRepository.deleteAll(categoryItems);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(MESSAGE_404_ItemNotFound));
        itemRepository.delete(item);
    }

    private Item createItemEntity(ItemRequestDto itemRequestDto) {
        ItemMapper itemMapper = new ItemMapper();
        Item item = itemMapper.mapToItem(itemRequestDto);
        Item savedItem = itemRepository.save(item);
        return savedItem;
    }

    private void saveCategoryItems(ItemRequestDto itemRequestDto, Item savedItem) {
        CategoryItemMapper categoryItemMapper = new CategoryItemMapper();
        for (Long categoryId : itemRequestDto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(MESSAGE_404_CategoryNotFound));
            CategoryItem categoryItem = categoryItemMapper.mapToCategoryItem(savedItem, category);
            categoryItemRepository.save(categoryItem);
        }
    }

    private List<String> getCategoryNames(ItemRequestDto itemRequestDto) {
        return categoryRepository.findAllById(itemRequestDto.getCategoryIds())
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}

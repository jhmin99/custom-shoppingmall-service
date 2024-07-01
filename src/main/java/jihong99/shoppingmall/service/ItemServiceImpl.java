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

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements IItemService{
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryItemRepository categoryItemRepository;

    @Transactional
    @Override
    public ItemResponseDto createItem(ItemRequestDto itemRequestDto) {
        ItemMapper itemMapper = new ItemMapper();
        Item item = itemMapper.mapToItem(itemRequestDto);
        Item savedItem = itemRepository.save(item);

        CategoryItemMapper categoryItemMapper = new CategoryItemMapper();
        for (Long categoryId : itemRequestDto.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId));
            CategoryItem categoryItem = categoryItemMapper.mapToCategoryItem(savedItem, category);
            categoryItemRepository.save(categoryItem);
        }

        List<String> categoryNames = categoryRepository.findAllById(itemRequestDto.getCategoryIds())
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        ItemResponseDto itemResponseDto = ItemResponseDto.of(savedItem.getId(), savedItem.getName(), savedItem.getPrice(), savedItem.getInventory(),
                savedItem.getKeyword(), savedItem.getRegistrationDate(), categoryNames);
        return itemResponseDto;
    }
}

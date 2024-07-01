package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.entity.Item;

public class ItemMapper {
    public Item mapToItem(ItemRequestDto itemRequestDto){
        Item item = Item.builder()
                .name(itemRequestDto.getName())
                .price(itemRequestDto.getPrice())
                .inventory(itemRequestDto.getInventory())
                .keyword(itemRequestDto.getKeyword())
                .build();
        return item;
    }
}

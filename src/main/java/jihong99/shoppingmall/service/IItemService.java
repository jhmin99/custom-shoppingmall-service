package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.ItemRequestDto;
import jihong99.shoppingmall.dto.ItemResponseDto;

public interface IItemService {
    ItemResponseDto createItem(ItemRequestDto itemRequestDto);
}

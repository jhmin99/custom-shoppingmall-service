package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.cart.CartItemRequestDto;
import jihong99.shoppingmall.dto.request.cart.UpdateQuantityRequestDto;

public interface ICartService {

    void updateItemQuantity(Long userId, Long itemId, UpdateQuantityRequestDto updateQuantityRequestDto);

    void addCartItem(Long userId, Long itemId, CartItemRequestDto cartItemRequestDto);

    void removeCartItem(Long userId, Long itemId);
}

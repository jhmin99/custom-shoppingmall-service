package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.cart.CartItemRequestDto;
import jihong99.shoppingmall.dto.request.cart.UpdateQuantityRequestDto;
import jihong99.shoppingmall.entity.Cart;
import jihong99.shoppingmall.entity.CartItem;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CartItemRepository;
import jihong99.shoppingmall.repository.CartRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService{
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    /**
     * Adds an item to the user's cart. If the item is already in the cart, updates the quantity.
     * Recalculates the cart's total prices after the addition.
     *
     * @param userId The ID of the user.
     * @param itemId The ID of the item to add.
     * @param cartItemRequestDto Contains the quantity of the item to add.
     */
    @Override
    @Transactional
    public void addCartItem(Long userId, Long itemId, CartItemRequestDto cartItemRequestDto) {
        Users user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        Cart cart = user.getCart();

        validateStockAvailability(item, cartItemRequestDto.getQuantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId())
                .orElseGet(() -> createNewCartItem(cart, item, cartItemRequestDto.getQuantity()));

        updateCartItemQuantity(cartItem, cartItemRequestDto.getQuantity());

        cart.recalculateTotalPrices();
        cartItemRepository.save(cartItem);
    }

    /**
     * Updates the quantity of an item in the user's cart.
     * Recalculates the cart's total prices after updating the quantity.
     *
     * @param userId The ID of the user.
     * @param itemId The ID of the item to update.
     * @param updateQuantityRequestDto Contains the new quantity for the item.
     */
    @Override
    @Transactional
    public void updateItemQuantity(Long userId, Long itemId, UpdateQuantityRequestDto updateQuantityRequestDto) {
        Users user = findUserOrThrow(userId);
        Cart cart = user.getCart();
        Item item = findItemOrThrow(itemId);
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_CartItemNotFound));

        validateStockAvailability(item, updateQuantityRequestDto.getQuantity());
        cartItem.updateQuantity(updateQuantityRequestDto.getQuantity());
        cart.recalculateTotalPrices();

        cartItemRepository.save(cartItem);
    }

    /**
     * Removes an item from the user's cart and recalculates the total prices.
     *
     * @param userId The ID of the user.
     * @param itemId The ID of the item to remove.
     */
    @Override
    @Transactional
    public void removeCartItem(Long userId, Long itemId) {
        Users user = findUserOrThrow(userId);
        Cart cart = user.getCart();
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_CartItemNotFound));

        cartItemRepository.delete(cartItem);

        cart.recalculateTotalPrices();

        cartRepository.save(cart);
    }



    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ItemNotFound));
    }

    private CartItem createNewCartItem(Cart cart, Item item, int quantity) {
        CartItem newCartItem = CartItem.createCartItem(cart, item, quantity, item.getPrice());
        cartItemRepository.save(newCartItem);
        return newCartItem;
    }

    private void updateCartItemQuantity(CartItem cartItem, int additionalQuantity) {
        cartItem.updateQuantity(cartItem.getQuantity() + additionalQuantity);
    }

    private void validateStockAvailability(Item item, int quantityRequested) {
        if (item.getStock() < quantityRequested) {
            throw new InvalidOperationException(MESSAGE_400_StockUnavailable);
        }
    }
}

package jihong99.shoppingmall.service;

public interface IWishListService {
    void addWishItem(Long userId, Long itemId);

    void removeWishItem(Long userId, Long itemId);

}

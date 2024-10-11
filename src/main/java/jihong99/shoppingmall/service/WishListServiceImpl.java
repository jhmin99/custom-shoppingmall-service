package jihong99.shoppingmall.service;

import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.WishItem;
import jihong99.shoppingmall.entity.WishList;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.ItemRepository;
import jihong99.shoppingmall.repository.UserRepository;
import jihong99.shoppingmall.repository.WishItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jihong99.shoppingmall.constants.Constants.MESSAGE_404_ItemNotFound;
import static jihong99.shoppingmall.constants.Constants.MESSAGE_404_UserNotFound;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements  IWishListService{

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final WishItemRepository wishItemRepository;

    @Override
    @Transactional
    public void addWishItem(Long userId, Long itemId) {
        Users user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        WishList wishList = user.getWishList();

        wishItemRepository.findByUsersIdAndItemId(userId, itemId)
                .ifPresentOrElse(
                        wishItem -> {

                        },
                        () -> {
                            WishItem newWishItem = WishItem.createWishItem(item, wishList);
                            wishList.addWishItem(newWishItem);
                            wishItemRepository.save(newWishItem);
                        }
                );
    }

    @Override
    @Transactional
    public void removeWishItem(Long userId, Long itemId) {
        Users user = findUserOrThrow(userId);
        WishList wishList = user.getWishList();
        WishItem wishItem = findWishItemOrThrow(userId, itemId);

        wishList.removeWishItem(wishItem);
        wishItemRepository.delete(wishItem);
    }


    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_ItemNotFound));
    }

    private WishItem findWishItemOrThrow(Long userId, Long itemId) {
        return wishItemRepository.findByUsersIdAndItemId(userId, itemId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_WishItemNotFound)
        );
    }

}

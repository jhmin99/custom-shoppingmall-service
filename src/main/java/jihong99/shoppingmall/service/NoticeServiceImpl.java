package jihong99.shoppingmall.service;

import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.request.notice.NoticeRequestDto;
import jihong99.shoppingmall.dto.request.notice.PatchNoticeRequestDto;
import jihong99.shoppingmall.entity.*;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements INoticeService {

    private final NoticeRepository noticeRepository;
    private final UserNoticeRepository userNoticeRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemAlertRepository itemAlertRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Creates a new notice.
     *
     * @param noticeRequestDto The DTO containing the notice details.
     */
    @Override
    public void createNotice(NoticeRequestDto noticeRequestDto) {
        Notice notice = Notice.createNotice(
                noticeRequestDto.getTitle(),
                noticeRequestDto.getContent()
        );
        noticeRepository.save(notice);
    }

    /**
     * Assigns a notice to a specific user.
     *
     * @param noticeId The ID of the notice.
     * @param userId The ID of the user.
     */
    @Override
    @Transactional
    public void postNoticeToUser(Long noticeId, Long userId) {
        Notice notice = validateNoticesExist(noticeId);
        Users user = validateUsersExist(userId);
        UserNotice userNotice = UserNotice.createUserNotice(user, notice);
        userNoticeRepository.save(userNotice);
    }

    /**
     * Validates if the user with the specified ID exists.
     *
     * @param userId The ID of the user.
     * @return The found user.
     * @throws NotFoundException if the user does not exist.
     */
    private Users validateUsersExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_UserNotFound)
        );
    }

    /**
     * Validates if the notice with the specified ID exists.
     *
     * @param noticeId The ID of the notice.
     * @return The found notice.
     * @throws NotFoundException if the notice does not exist.
     */
    private Notice validateNoticesExist(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_NoticeNotFound)
        );
    }

    /**
     * Assigns a notice to all users.
     *
     * @param noticeId The ID of the notice.
     */
    @Override
    @Transactional
    public void postNoticeToAllUsers(Long noticeId) {
        Notice notice = validateNoticesExist(noticeId);
        List<Users> users = userRepository.findAll();
        List<UserNotice> userNotices = getUserNotices(users, notice);
        userNoticeRepository.saveAll(userNotices);
    }

    /**
     * Updates the details of an existing notice.
     *
     * @param noticeId The ID of the notice to be updated.
     * @param patchNoticeRequestDto The DTO containing the updated notice details.
     */
    @Override
    @Transactional
    public void patchNotice(Long noticeId, PatchNoticeRequestDto patchNoticeRequestDto) {
        Notice notice = validateNoticesExist(noticeId);
        if (patchNoticeRequestDto.getTitle() != null && !patchNoticeRequestDto.getTitle().isEmpty()) {
            notice.updateTitle(patchNoticeRequestDto.getTitle());
        }
        if (patchNoticeRequestDto.getContent() != null && !patchNoticeRequestDto.getContent().isEmpty()) {
            notice.updateContent(patchNoticeRequestDto.getContent());
        }

        noticeRepository.save(notice);
    }

    /**
     * Deletes a notice and all associated user notices.
     *
     * @param noticeId The ID of the notice to be deleted.
     */
    @Override
    public void deleteNotice(Long noticeId) {
        Notice notice = validateNoticesExist(noticeId);
        userNoticeRepository.deleteAllByNoticeId(notice.getId());
        noticeRepository.delete(notice);
    }

    /**
     * Sends a stock alert notice to users who are tracking an item.
     *
     * @param itemId The ID of the item that is back in stock.
     */
    @Override
    @Transactional
    public void notifyStockAlertToUsers(Long itemId) {
        Item item = validateItemsExist(itemId);
        String title = "Stock Alert: " + item.getName();
        String content = String.format("The item '%s' is back in stock. Don't miss out!", item.getName());
        Notice notice = Notice.createNotice(title, content);
        List<Users> users = itemAlertRepository.findAllByItemId(itemId);
        if (users.isEmpty()) {
            return;
        }
        List<UserNotice> userNotices = getUserNotices(users, notice);
        userNoticeRepository.saveAll(userNotices);
    }

    /**
     * Creates a list of user notices based on the list of users and a notice.
     *
     * @param users The list of users to whom the notice will be sent.
     * @param notice The notice to be sent to users.
     * @return A list of UserNotice entities.
     */
    private static List<UserNotice> getUserNotices(List<Users> users, Notice notice) {
        return users.stream()
                .map(user -> UserNotice.createUserNotice(user, notice))
                .collect(Collectors.toList());
    }

    /**
     * Validates if the item with the specified ID exists.
     *
     * @param itemId The ID of the item.
     * @return The found item.
     * @throws NotFoundException if the item does not exist.
     */
    private Item validateItemsExist(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(Constants.MESSAGE_404_ItemNotFound)
        );
    }

    /**
     * Sends a cart item invalidation notice to users who have an invalidated item in their cart.
     *
     * @param itemId The ID of the item that has been invalidated.
     */
    @Override
    @Transactional
    public void notifyCartItemInvalidationToUsers(Long itemId) {
        Item item = validateItemsExist(itemId);
        String title = "Cart Item Invalidation Notice: " + item.getName();
        String content = String.format("The item '%s' in your cart has been invalidated and is no longer available for purchase.", item.getName());
        Notice notice = Notice.createNotice(title, content);
        List<CartItem> cartItems = cartItemRepository.findByItemId(itemId);

        if (cartItems.isEmpty()) {
            return;
        }

        List<Users> users = getUsers(cartItems);
        List<UserNotice> userNotices = getUserNotices(users, notice);
        userNoticeRepository.saveAll(userNotices);
    }

    /**
     * Retrieves the list of users who have a specific item in their cart.
     *
     * @param cartItems The list of cart items containing the item.
     * @return A list of users who have the item in their cart.
     */
    private List<Users> getUsers(List<CartItem> cartItems) {
        List<Long> cartIds = cartItems.stream()
                .map(cartItem -> cartItem.getCart().getId())
                .distinct()
                .collect(Collectors.toList());

        return userRepository.findUsersByCartIds(cartIds);
    }
}

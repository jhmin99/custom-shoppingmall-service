package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.notice.NoticeRequestDto;
import jihong99.shoppingmall.dto.request.notice.PatchNoticeRequestDto;
import jihong99.shoppingmall.dto.response.notice.NoticeDetailsResponseDto;
import jihong99.shoppingmall.dto.response.notice.NoticeResponseDto;
import jihong99.shoppingmall.entity.*;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

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
        Notice notice = findNoticeOrThrow(noticeId);
        Users user = findUserOrThrow(userId);
        UserNotice userNotice = UserNotice.createUserNotice(user, notice);
        userNoticeRepository.save(userNotice);
    }

    /**
     * Assigns a notice to all users.
     *
     * @param noticeId The ID of the notice.
     */
    @Override
    @Transactional
    public void postNoticeToAllUsers(Long noticeId) {
        Notice notice = findNoticeOrThrow(noticeId);
        List<Users> users = userRepository.findAll();
        List<UserNotice> userNotices = createUserNotices(users, notice);
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
        Notice notice = findNoticeOrThrow(noticeId);
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
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = findNoticeOrThrow(noticeId);
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
        Item item = findItemOrThrow(itemId);
        String title = "Stock Alert: " + item.getName();
        String content = String.format("The item '%s' is back in stock. Don't miss out!", item.getName());
        Notice notice = Notice.createNotice(title, content);
        List<ItemAlert> itemAlerts = itemAlertRepository.findAllByItemId(itemId);
        if (itemAlerts.isEmpty()) {
            return;
        }
        List<UserNotice> userNotices = createUserNoticesFromAlerts(itemAlerts, notice);

        userNoticeRepository.saveAll(userNotices);
    }

    /**
     * Sends a cart item invalidation notice to users who have an invalidated item in their cart.
     *
     * @param itemId The ID of the item that has been invalidated.
     */
    @Override
    @Transactional
    public void notifyCartItemInvalidationToUsers(Long itemId) {
        Item item = findItemOrThrow(itemId);
        String title = "Cart Item Invalidation Notice: " + item.getName();
        String content = String.format("The item '%s' in your cart has been invalidated and is no longer available for purchase.", item.getName());
        Notice notice = Notice.createNotice(title, content);
        List<CartItem> cartItems = cartItemRepository.findByItemId(itemId);

        if (cartItems.isEmpty()) {
            return;
        }

        List<Users> users = findUsersByCartItems(cartItems);
        List<UserNotice> userNotices = createUserNotices(users, notice);
        userNoticeRepository.saveAll(userNotices);
    }
    /**
     * Retrieves a paginated list of all notices for a specific user.
     *
     * @param userId   The ID of the user whose notices are being retrieved.
     * @param pageable The pagination information.
     * @return A paginated list of NoticeResponseDto objects.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getAllNotices(Long userId, Pageable pageable) {
        return userNoticeRepository.findAllByUsersId(userId, pageable)
                .map(NoticeServiceImpl::convertToNoticeResponseDto);
    }


    /**
     * Retrieves the details of a specific notice for a user.
     *
     * @param userId   The ID of the user associated with the notice.
     * @param noticeId The ID of the notice to retrieve.
     * @return A NoticeDetailsResponseDto containing detailed notice information.
     */
    @Override
    @Transactional(readOnly = true)
    public NoticeDetailsResponseDto getNoticeDetails(Long userId, Long noticeId) {
        UserNotice userNotice = findUserNoticeOrThrow(userId, noticeId);
        return convertToNoticeDetailsResponseDto(userNotice);
    }


    private static List<UserNotice> createUserNoticesFromAlerts(List<ItemAlert> itemAlerts, Notice notice) {
        return itemAlerts.stream()
                .map(itemAlert -> {
                    Users user = itemAlert.getUsers();
                    return UserNotice.createUserNotice(user, notice);
                })
                .collect(Collectors.toList());
    }


    private List<Users> findUsersByCartItems(List<CartItem> cartItems) {
        List<Long> cartIds = cartItems.stream()
                .map(cartItem -> cartItem.getCart().getId())
                .distinct()
                .collect(Collectors.toList());

        return userRepository.findUsersByCartIds(cartIds);
    }

    private static List<UserNotice> createUserNotices(List<Users> users, Notice notice) {
        return users.stream()
                .map(user -> UserNotice.createUserNotice(user, notice))
                .collect(Collectors.toList());
    }

    private Item findItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_ItemNotFound)
        );
    }

    private Notice findNoticeOrThrow(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_NoticeNotFound)
        );
    }

    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_UserNotFound)
        );
    }


    private static NoticeResponseDto convertToNoticeResponseDto(UserNotice notice) {
        return NoticeResponseDto.of(
                notice.getNotice().getId(),
                notice.getNotice().getTitle(),
                notice.getNotice().getRegistrationDate());
    }


    private UserNotice findUserNoticeOrThrow(Long userId, Long noticeId) {
        return userNoticeRepository.findByUsersIdAndNoticeId(userId, noticeId).orElseThrow(() -> new NotFoundException(MESSAGE_404_NoticeNotFound));
    }

    private static NoticeDetailsResponseDto convertToNoticeDetailsResponseDto(UserNotice userNotice) {
        return NoticeDetailsResponseDto.of(
                userNotice.getNotice().getTitle(),
                userNotice.getNotice().getContent(),
                userNotice.getNotice().getRegistrationDate()
        );
    }

}

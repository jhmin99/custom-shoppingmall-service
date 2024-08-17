package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.notice.NoticeRequestDto;
import jihong99.shoppingmall.dto.request.notice.PatchNoticeRequestDto;

public interface INoticeService {
    void createNotice(NoticeRequestDto noticeRequestDto);
    void postNoticeToUser(Long noticeId, Long userId);
    void postNoticeToAllUsers(Long noticeId);
    void patchNotice(Long noticeId, PatchNoticeRequestDto patchNoticeRequestDto);

    void deleteNotice(Long noticeId);
    void notifyStockAlertToUsers(Long itemId);
    void notifyCartItemInvalidationToUsers(Long itemId);
}

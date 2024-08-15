package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNoticeRepository extends JpaRepository<UserNotice, Long> {
    void deleteAllByNoticeId(Long noticeId);
}

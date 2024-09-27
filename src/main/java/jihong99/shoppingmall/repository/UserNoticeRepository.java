package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.UserNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserNoticeRepository extends JpaRepository<UserNotice, Long> {
    void deleteAllByNoticeId(Long noticeId);
    Page<UserNotice> findAllByUsersId(Long userId, Pageable pageable);

    Optional<UserNotice> findByUsersIdAndNoticeId(Long userId, Long noticeId);
}

package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.WishItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishItemRepository extends JpaRepository<WishItem, Long> {
    Optional<WishItem> findByUsersIdAndItemId(Long userId, Long itemId);
}

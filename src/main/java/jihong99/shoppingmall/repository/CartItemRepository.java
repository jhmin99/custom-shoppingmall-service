package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByItemId(Long itemId);
    Optional<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);
}

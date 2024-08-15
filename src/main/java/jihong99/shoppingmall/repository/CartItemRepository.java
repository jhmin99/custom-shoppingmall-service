package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByItemId(Long itemId);
}

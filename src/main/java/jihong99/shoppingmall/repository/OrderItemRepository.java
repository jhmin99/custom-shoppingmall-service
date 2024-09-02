package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrdersId(Long OrderId);
}

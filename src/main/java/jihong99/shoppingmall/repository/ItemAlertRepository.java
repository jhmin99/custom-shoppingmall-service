package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.ItemAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemAlertRepository extends JpaRepository<ItemAlert, Long> {
    List<ItemAlert> findAllByItemId(Long itemId);
}

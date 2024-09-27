package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.ItemAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ItemAlertRepository extends JpaRepository<ItemAlert, Long> {
    List<ItemAlert> findAllByItemId(Long itemId);
}

package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository  extends JpaRepository<Item, Long> {
}

package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.CategoryItem;
import jihong99.shoppingmall.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
    List<CategoryItem> findByItemId(Long itemId);

    List<CategoryItem> findByCategoryId(Long categoryId);

    void deleteAllByItem(Item item);
}

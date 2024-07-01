package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
}

package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}

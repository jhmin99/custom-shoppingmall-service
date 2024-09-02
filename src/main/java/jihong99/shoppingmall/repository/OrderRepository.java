package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByUsersId(Long userId);
}

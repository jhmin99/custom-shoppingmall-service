package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.OrderDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
    Page<OrderDetails> findAllByOrdersId(Long OrderId, Pageable pageable);
}

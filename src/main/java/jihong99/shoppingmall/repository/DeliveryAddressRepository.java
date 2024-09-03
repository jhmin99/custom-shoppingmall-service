package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Set<DeliveryAddress> findAllByUsersId(Long id);

}

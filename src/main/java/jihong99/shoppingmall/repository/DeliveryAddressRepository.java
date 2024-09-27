package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    Set<DeliveryAddress> findAllByUsersId(Long id);

}

package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.dto.UserSummaryDto;
import jihong99.shoppingmall.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByIdentification(String identification);
    @Query("SELECT new jihong99.shoppingmall.dto.UserSummaryDto(" +
            "u.id, u.name, u.birthDate, da.id, da.zipCode, da.address, da.addressDetail, u.registrationDate) " +
            "FROM Users u LEFT JOIN DeliveryAddress da ON da.users.id = u.id WHERE u.role = 'USER'")
    Page<UserSummaryDto> findAllUserSummaries(Pageable pageable);
}



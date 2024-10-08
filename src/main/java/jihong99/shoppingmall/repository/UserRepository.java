package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByIdentification(String identification);

    Page<Users> findAllByRole(Roles role, Pageable pageable);

    List<Users> findAllByRole(Roles role);
    @Query("SELECT u FROM Users u WHERE u.cart.id IN :cartIds")
    List<Users> findUsersByCartIds(@Param("cartIds") List<Long> cartIds);

}



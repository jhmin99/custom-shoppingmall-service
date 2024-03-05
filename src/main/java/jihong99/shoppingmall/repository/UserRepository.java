package jihong99.shoppingmall.repository;

import jihong99.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdentification(String identification);
}

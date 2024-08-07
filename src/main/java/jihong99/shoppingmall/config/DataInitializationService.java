package jihong99.shoppingmall.config;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.repository.CouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static jihong99.shoppingmall.entity.enums.Roles.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class DataInitializationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superAdmin.id}")
    private String superAdminIdentification;

    @Value("${superAdmin.pw}")
    private String superAdminPassword;
    @Transactional
    public void initializeData() {
        createSuperAdmin();
    }

    private void createSuperAdmin() {
        if (userRepository.findByIdentification(superAdminIdentification).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(superAdminPassword);
            Users superAdmin = Users.createSuperAdmin(superAdminIdentification,encodedPassword);
            userRepository.save(superAdmin);
        }
    }
}

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
    private final CouponRepository couponRepository;

    @Value("${superAdmin.id}")
    private String superAdminIdentification;

    @Value("${superAdmin.pw}")
    private String superAdminPassword;
    @Transactional
    public void initializeData() {
        createSuperAdmin();
        createWelcomeCoupon();
    }

    private void createSuperAdmin() {
        if (userRepository.findByIdentification(superAdminIdentification).isEmpty()) {
            Users superAdmin = Users.builder()
                    .identification(superAdminIdentification)
                    .password(passwordEncoder.encode(superAdminPassword))
                    .build();
            superAdmin.updateRole(SUPER_ADMIN);
            userRepository.save(superAdmin);
        }
    }

    private void createWelcomeCoupon(){
        if (couponRepository.findByName("Welcome Coupon").isEmpty()) {
            Coupon welcomeCoupon = Coupon.builder()
                    .name("Welcome Coupon")
                    .content("Welcome! Enjoy your first purchase with this coupon.")
                    .expirationDate(LocalDate.now().plusYears(999))
                    .build();
            couponRepository.save(welcomeCoupon);
        }
    }
}

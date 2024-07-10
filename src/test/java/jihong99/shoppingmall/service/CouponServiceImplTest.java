package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.dto.CouponResponseDto;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.UserCoupon;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.entity.enums.Tiers;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.InvalidExpirationDateException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.CouponRepository;
import jihong99.shoppingmall.repository.UserCouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static jihong99.shoppingmall.entity.enums.Tiers.IRON;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CouponServiceImplTest {

    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ICouponService couponService;

    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){
        userCouponRepository.deleteAll();
    }

    private void createWelcomeCoupon() {
        if (couponRepository.findByName("Welcome Coupon").isEmpty()) {
            Coupon welcomeCoupon = Coupon.builder()
                    .name("Welcome Coupon")
                    .content("Welcome! Enjoy your first purchase with this coupon.")
                    .expirationDate(LocalDate.now().plusYears(999))
                    .build();
            couponRepository.save(welcomeCoupon);
        }
    }

    /**
     * Test method to retrieve user coupons successfully.
     * Ensures the user has the correct valid coupon assigned.
     */
    @Test
    @Transactional
    void getUserCoupons_Success() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();
        UserCoupon userCoupon = UserCoupon.createUserCoupon(user, coupon);
        userCouponRepository.save(userCoupon);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CouponResponseDto> coupons = couponService.getUserCoupons(user.getId(), pageable);

        // then
        assertThat(coupons).isNotNull();
        assertThat(coupons.getTotalElements()).isEqualTo(1);
        assertThat(coupons.getContent().get(0).getName()).isEqualTo("Welcome Coupon");
    }

    /**
     * Test method to ensure expired coupons are not retrieved.
     * Ensures the user does not have expired coupons.
     */
    @Test
    @Transactional
    void getUserCoupons_ExpiredCoupon() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        Coupon validCoupon = couponRepository.findByName("Welcome Coupon").get();
        UserCoupon validUserCoupon = UserCoupon.createUserCoupon(user, validCoupon);
        userCouponRepository.save(validUserCoupon);

        Coupon expiredCoupon = Coupon.builder()
                .name("Expired Coupon")
                .content("This is an expired coupon.")
                .expirationDate(LocalDate.now().minusDays(1))
                .build();
        couponRepository.save(expiredCoupon);

        UserCoupon expiredUserCoupon = UserCoupon.createUserCoupon(user, expiredCoupon);
        userCouponRepository.save(expiredUserCoupon);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CouponResponseDto> coupons = couponService.getUserCoupons(user.getId(), pageable);

        // then
        assertThat(coupons).isNotNull();
        assertThat(coupons.getTotalElements()).isEqualTo(1);
        assertThat(coupons.getContent().get(0).getName()).isEqualTo("Welcome Coupon");

        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        createWelcomeCoupon();
    }

    /**
     * Test method to handle NotFoundException when retrieving coupons for a non-existent user.
     */
    @Test
    void getUserCoupons_NotFoundException() {
        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.getUserCoupons(-1L, PageRequest.of(0, 10));
        });
    }

    /**
     * Test method to successfully create a new coupon.
     * Ensures the coupon is created with the correct details.
     */
    @Test
    @Transactional
    void createCoupon_Success() {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("Test Coupon", "Test Content", LocalDate.now().plusDays(10));

        // when
        CouponResponseDto responseDto = couponService.createCoupon(requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getName()).isEqualTo("Test Coupon");

        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        createWelcomeCoupon();
    }

    /**
     * Test method to handle DuplicateNameException when creating a coupon with an existing name.
     */
    @Test
    void createCoupon_DuplicateNameException() {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("Welcome Coupon", "Test Content", LocalDate.now().plusDays(10));

        // when & then
        assertThrows(DuplicateNameException.class, () -> {
            couponService.createCoupon(requestDto);
        });
    }

    /**
     * Test method to handle InvalidExpirationDateException when creating a coupon with an expired date.
     */
    @Test
    void createCoupon_InvalidExpirationDateException() {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("New Coupon", "Test Content", LocalDate.now().minusDays(1));

        // when & then
        assertThrows(InvalidExpirationDateException.class, () -> {
            couponService.createCoupon(requestDto);
        });
    }

    /**
     * Test method to retrieve all coupons with pagination.
     * Ensures all coupons are correctly retrieved.
     */
    @Test
    void getAllCoupons_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<CouponResponseDto> coupons = couponService.getAllCoupons(pageable);

        // then
        assertThat(coupons).isNotNull();
        assertThat(coupons.getTotalElements()).isEqualTo(1);
    }

    /**
     * Test method to distribute a coupon to a specific user successfully.
     * Ensures the user receives the correct coupon.
     */
    @Test
    @Transactional
    void distributeCouponToUser_Success() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when
        couponService.distributeCouponToUser(coupon.getId(), user.getId());

        // then
        List<UserCoupon> userCoupons = userCouponRepository.findByUsersId(savedUser.getId());
        assertThat(userCoupons).isNotEmpty();
        assertThat(userCoupons.get(0).getCoupon().getName()).isEqualTo("Welcome Coupon");
    }

    /**
     * Test method to handle NotFoundException when distributing a coupon to a non-existent user.
     */
    @Test
    void distributeCouponToUser_NotFoundException_User() {
        // given
        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToUser(coupon.getId(), -1L);
        });
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to a user.
     */
    @Test
    @Transactional
    void distributeCouponToUser_NotFoundException_Coupon() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToUser(-1L, savedUser.getId());
        });
    }

    /**
     * Test method to distribute a coupon to users by their tier successfully.
     * Ensures the users in the specified tier receive the coupon.
     */
    @Test
    @Transactional
    void distributeCouponToUsersByTier_Success() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);
        savedUser.updateTier(IRON);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when
        couponService.distributeCouponToUsersByTier(coupon.getId(), IRON);

        // then
        List<UserCoupon> userCoupons = userCouponRepository.findByUsersId(savedUser.getId());
        assertThat(userCoupons).isNotEmpty();
        assertThat(userCoupons.get(0).getCoupon().getName()).isEqualTo("Welcome Coupon");
    }

    /**
     * Test method to handle NotFoundException when no users are found in the specified tier.
     */
    @Test
    void distributeCouponToUsersByTier_NotFoundException_User() {
        // given
        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToUsersByTier(coupon.getId(), Tiers.GOLD);
        });
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to users by their tier.
     */
    @Test
    @Transactional
    void distributeCouponToUsersByTier_NotFoundException_Coupon() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);
        savedUser.updateTier(IRON);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToUsersByTier(-1L, IRON);
        });
    }

    /**
     * Test method to distribute a coupon to all users successfully.
     * Ensures all users receive the coupon.
     */
    @Test
    @Transactional
    void distributeCouponToAllUsers_Success() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when
        couponService.distributeCouponToAllUsers(coupon.getId());

        // then
        List<UserCoupon> userCoupons = userCouponRepository.findByUsersId(savedUser.getId());
        assertThat(userCoupons).isNotNull();
        assertThat(userCoupons.get(0).getCoupon().getName()).isEqualTo("Welcome Coupon");
    }

    /**
     * Test method to handle NotFoundException when distributing a coupon to all users but no users are found.
     */
    @Test
    void distributeCouponToAllUsers_NotFoundException_User() {
        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToAllUsers(-1L);
        });
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to all users.
     */
    @Test
    @Transactional
    void distributeCouponToAllUsers_NotFoundException_Coupon() {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        // when & then
        assertThrows(NotFoundException.class, () -> {
            couponService.distributeCouponToAllUsers(-1L);
        });
    }
}

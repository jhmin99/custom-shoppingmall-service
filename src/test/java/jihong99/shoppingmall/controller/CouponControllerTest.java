package jihong99.shoppingmall.controller;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.constants.Constants;
import jihong99.shoppingmall.dto.CouponRequestDto;
import jihong99.shoppingmall.entity.Coupon;
import jihong99.shoppingmall.entity.UserCoupon;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.repository.CouponRepository;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserCouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static jihong99.shoppingmall.entity.enums.Tiers.*;
import static jihong99.shoppingmall.utils.JsonUtils.asJsonString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class CouponControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        userCouponRepository.deleteAll();
        deliveryAddressRepository.deleteAll();
        userRepository.deleteAll();
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
     * Test method to create a new coupon and return 201 Created.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void createCoupon_Return_Created() throws Exception {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("Test Coupon", "Test Content", LocalDate.now().plusDays(10));

        // when & then
        mockMvc.perform(post("/api/admin/coupon")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Coupon"));
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        createWelcomeCoupon();

    }

    /**
     * Test method to handle MethodArgumentNotValidException and return 400 Bad Request.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoupon_Return_BadRequest_Handles_MethodArgumentNotValidException() throws Exception {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("", "Test Content", LocalDate.now().plusDays(10));

        // when & then
        mockMvc.perform(post("/api/admin/coupon")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test method to handle DuplicateNameException and return 400 Bad Request.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoupon_Return_BadRequest_Handles_DuplicateNameException() throws Exception {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("Welcome Coupon", "Test Content", LocalDate.now().plusDays(10));

        // when & then
        mockMvc.perform(post("/api/admin/coupon")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.name()));
    }

    /**
     * Test method to handle InvalidExpirationDateException and return 400 Bad Request.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoupon_Return_BadRequest_Handles_InvalidExpirationDateException() throws Exception {
        // given
        CouponRequestDto requestDto = new CouponRequestDto("Test Coupon", "Test Content", LocalDate.now().minusDays(1));

        // when & then
        mockMvc.perform(post("/api/admin/coupon")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.name()));
    }

    /**
     * Test method to retrieve user coupons and return 200 OK.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "user1",roles = "USER")
    void getUserCoupons_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser = userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();
        UserCoupon userCoupon = UserCoupon.createUserCoupon(savedUser, coupon);
        userCouponRepository.save(userCoupon);

        // when & then
        mockMvc.perform(get("/api/users/coupons")
                        .param("userId", savedUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Welcome Coupon"));
    }

    /**
     * Test method to handle NotFoundException when retrieving user coupons and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "USER")
    void getUserCoupons_Return_NotFound_Handles_NotFoundException() throws Exception {
        // when & then
        mockMvc.perform(get("/api/users/coupons")
                        .param("userId", "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }
    /**
     * Test method to handle AccessDeniedException when retrieving user coupons and return 403 Forbidden.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void getUserCoupons_Return_Forbidden_Handles_AccessDeniedException() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);
        Users user2 = Users.builder()
                .identification("user2")
                .build();
        Users savedUser2 = userRepository.save(user2);

        // when & then
        mockMvc.perform(get("/api/users/coupons")
                        .param("userId", savedUser2.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.FORBIDDEN.name()));
    }

    /**
     * Test method to retrieve all coupons and return 200 OK.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCoupons_Return_OK() throws Exception {
        // when & then
        mockMvc.perform(get("/api/admin/coupons")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Welcome Coupon"));
    }

    /**
     * Test method to distribute a coupon to a specific user and return 200 OK.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUser_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/user")
                        .with(csrf())
                        .param("couponId", coupon.getId().toString())
                        .param("userId", user.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Test method to handle NotFoundException when distributing a coupon to a non-existent user and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUser_Return_NotFound_Handles_NotFoundException_User() throws Exception {
        // given
        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/user")
                        .with(csrf())
                        .param("couponId", coupon.getId().toString())
                        .param("userId", "-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to a user and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUser_Return_NotFound_Handles_NotFoundException_Coupon() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/user")
                        .with(csrf())
                        .param("couponId", "-1")
                        .param("userId", user.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Test method to distribute a coupon to users by tier and return 200 OK.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUsersByTier_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser  = userRepository.save(user);
        savedUser.updateTier(IRON);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/tier")
                        .with(csrf())
                        .param("couponId", coupon.getId().toString())
                        .param("tier", IRON.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Test method to handle NotFoundException when distributing a coupon to users of a non-existent tier and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUsersByTier_Return_NotFound_Handles_NotFoundException_User() throws Exception {
        // given
        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/tier")
                        .with(csrf())
                        .param("couponId", coupon.getId().toString())
                        .param("tier", GOLD.name()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to users by their tier and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToUsersByTier_Return_NotFound_Handles_NotFoundException_Coupon() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        Users savedUser  = userRepository.save(user);
        savedUser.updateTier(IRON);

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/tier")
                        .with(csrf())
                        .param("couponId", "-1")
                        .param("tier", IRON.name()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Test method to distribute a coupon to all users and return 200 OK.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToAllUsers_Return_OK() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        Coupon coupon = couponRepository.findByName("Welcome Coupon").get();

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/all")
                        .with(csrf())
                        .param("couponId", coupon.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusMessage").value(Constants.MESSAGE_200_DistributeCouponSuccess));
    }

    /**
     * Test method to handle NotFoundException when distributing a coupon to all users but no users are found and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToAllUsers_Return_NotFound_Handles_NotFoundException_User() throws Exception {
        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/all")
                        .with(csrf())
                        .param("couponId", "-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }

    /**
     * Test method to handle NotFoundException when distributing a non-existent coupon to all users and return 404 Not Found.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void distributeCouponToAllUsers_Return_NotFound_Handles_NotFoundException_Coupon() throws Exception {
        // given
        Users user = Users.builder()
                .identification("user1")
                .build();
        userRepository.save(user);

        // when & then
        mockMvc.perform(post("/api/admin/coupons/distribute/all")
                        .with(csrf())
                        .param("couponId", "-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.NOT_FOUND.name()));
    }
}

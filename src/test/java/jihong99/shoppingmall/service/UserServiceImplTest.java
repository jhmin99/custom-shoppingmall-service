package jihong99.shoppingmall.service;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.config.auth.providers.JwtTokenProvider;
import jihong99.shoppingmall.dto.*;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateNameException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserCouponRepository;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static jihong99.shoppingmall.entity.enums.Roles.*;
import static jihong99.shoppingmall.entity.enums.Tiers.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private IDeliveryAddressService deliveryAddressService;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){
        deliveryAddressRepository.deleteAll();
        userCouponRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Test method for successfully signing up a new user account.
     */
    @Test
    @Transactional
    public void signUpAccount_Success(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","abcd123!@#",
                "민지홍","1999-12-30","01012341234");
        // when
        userService.signUpAccount(signUpDto);
        Users findUser = userRepository.findByIdentification("abcd123").get();
        // then
        assertThat(findUser.getId()).isNotNull();
        assertThat(findUser.getIdentification()).isEqualTo("abcd123");
        assertThat(passwordEncoder.matches("abcd123!@#", findUser.getPassword())).isTrue();
        assertThat(findUser.getName()).isEqualTo("민지홍");
        assertThat(findUser.getBirthDate()).isEqualTo(LocalDate.parse("1999-12-30"));
        assertThat(findUser.getPhoneNumber()).isEqualTo("01012341234");

        assertThat(findUser.getPoint()).isEqualTo(0);
        assertThat(findUser.getTier()).isEqualTo(IRON);
        assertThat(findUser.getAmountToNextTier()).isEqualTo(50000);
        assertThat(findUser.getRole()).isEqualTo(USER);

        assertThat(findUser.getCart()).isNotNull();
        assertThat(findUser.getCart().getEstimatedTotalPrice()).isEqualTo(0L);
        assertThat(findUser.getWishList()).isNotNull();

        assertThat(findUser.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(findUser.getCreationTime()).isNotNull();
        assertThat(findUser.getLastModifiedTime()).isNotNull();

    }

    /**
     * Test method while signing up a new user account throws DuplicateIdentificationException
     */
    @Test
    @Transactional
    public void signUpAccount_DuplicateIdentificationException(){
        // given
        Users users = Users.builder()
                .identification("abc123")
                .build();
        userRepository.save(users);
        // when & then
        SignUpDto signUpDto = new SignUpDto("abc123","abcd123!@#","abcd123!@#",
                "민지홍","1999-12-30","01012341234");
        assertThrows(DuplicateNameException.class,()->{
                    userService.signUpAccount(signUpDto);
                });
    }

    /**
     * Test method while signing up a new user account throws PasswordMismatchException
     */
    @Test
    public void signUpAccount_PasswordMismatchException(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","aaaa!@#",
                "민지홍","1999-12-30","01012341234");
        // when & then
        PasswordMismatchException passwordMismatchException = assertThrows(PasswordMismatchException.class, () -> {
            userService.signUpAccount(signUpDto);
        });
        assertThat(passwordMismatchException.getMessage()).isEqualTo("Passwords do not match.");
    }

    /**
     * Test method while signing up a new user account throws DateTimeParseException
     */

    @ParameterizedTest
    @ValueSource(strings = {"1999-15-30", "1999-12-32", "1999-12", "19991230"})
    public void signUpAccount_DateTimeParseException(String birthDate){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","abcd123!@#",
                "민지홍",birthDate,"01012341234");
        // when & then
        assertThrows(DateTimeParseException.class, () -> {
            userService.signUpAccount(signUpDto);
        });
    }


    /**
     * Test method while signing up a new user account with invalid SignUpDto field (confirmPassword, birthDate)
     */
    @Test
    public void signUpAccount_Both_PasswordMismatchException_And_DateTimeParseException(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","aaaa123!@#",
                "민지홍","1999-12-30","01012341234");
        // when & then
        assertThrows(PasswordMismatchException.class, ()->{
            userService.signUpAccount(signUpDto);
        });
    }

    /**
     * Test method for checking identification duplicated with no exception thrown
     */
    @Test
    public void checkDuplicateIdentification_Success(){
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","abcd123!@#",
                "민지홍","1999-12-30","01012341234");
        // when
        userService.checkDuplicateIdentification(signUpDto.getIdentification());
        // then: No exception should be thrown
    }

    /**
     * Tests the scenario that user repository already has a same identification
     */
    @Test
    @Transactional
    public void checkDuplicateIdentification_DuplicateIdentificationException(){
        // given
        Users users = Users.builder()
                .identification("abcd123")
                .password("aaa123!@#")
                .name("민지홍")
                .phoneNumber("010-1234-1234")
                .birthDate(LocalDate.parse("1999-12-30"))
                .build();
        userRepository.save(users);

        SignUpDto signUpDto = new SignUpDto("abcd123","abcd123!@#","abcd123!@#",
                "민지홍","1999-12-30","01012341234");
        // when & then
        DuplicateNameException duplicateNameException = assertThrows(DuplicateNameException.class, () -> {
            userService.checkDuplicateIdentification(signUpDto.getIdentification());
        });
        assertThat(duplicateNameException.getMessage()).isEqualTo("The ID already exists.");
    }

    /**
     * Test method for successfully logging in a user account.
     */
    @Test
    @Transactional
    public void loginByIdentificationAndPassword_Success() {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);

        LoginRequestDto loginRequestDto = new LoginRequestDto("abcd123", "abcd123!@#");

        // when
        Users user = userService.loginByIdentificationAndPassword(loginRequestDto);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(((UserDetailsDto) authentication.getPrincipal()).getUsername()).isEqualTo("abcd123");

        Users loggedInUser = userRepository.findById(user.getId()).orElse(null);
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getRefreshToken()).isNotNull();
    }

    /**
     * Test method for unsuccessful login due to wrong credentials.
     */
    @ParameterizedTest
    @CsvSource({
            "'abcd123', 'wrongPassword'",  // wrong password
            "'wrongId', 'abcd123!@#'",     // wrong identification
            "'wrongId', 'wrongPassword'",   // wrong identification and password
            "'', 'abcd123!@#'", // empty identification
            "'abcd123', ''", // empty password
    })
    @Transactional
    public void loginByIdentificationAndPassword_BadCredentialsException(String identification, String password) {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);

        LoginRequestDto loginRequestDto = new LoginRequestDto(identification, password);

        // when & then
        assertThrows(BadCredentialsException.class, () -> {
            userService.loginByIdentificationAndPassword(loginRequestDto);
        });
    }

    @Test
    @Transactional
    public void getUserDetails_Success_Without_DeliveryAddress() {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);
        Users findUser = userRepository.findByIdentification("abcd123").get();

        // when
        MyPageResponseDto myPageResponseDto = userService.getUserDetails(findUser.getId());

        // then
        assertThat(myPageResponseDto).isNotNull();
        assertThat(myPageResponseDto.getIdentification()).isEqualTo("abcd123");
        assertThat(myPageResponseDto.getName()).isEqualTo("민지홍");
        assertThat(myPageResponseDto.getBirthDate()).isEqualTo(LocalDate.parse("1999-12-30"));
        assertThat(myPageResponseDto.getPhoneNumber()).isEqualTo("01012341234");
        assertThat(myPageResponseDto.getDeliveryAddresses().isEmpty()).isTrue(); // Assuming delivery addresses are empty

    }
    @Test
    @Transactional
    public void getUserDetails_With_DeliveryAddresses() {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);
        Users findUser = userRepository.findByIdentification("abcd123").get();
        DeliveryAddressDto deliveryAddress1 = new DeliveryAddressDto(findUser.getId(),null,"지홍민1","01012341234"
                ,14235,"abc로 123길", "101-1234");
        deliveryAddressService.addDeliveryAddress(deliveryAddress1);
        DeliveryAddressDto deliveryAddress2 = new DeliveryAddressDto(findUser.getId(),null,"지홍민2","01012341234"
                ,14235,"abc로 123길", "101-1234");
        deliveryAddressService.addDeliveryAddress(deliveryAddress2);

        // when
        MyPageResponseDto myPageResponseDto = userService.getUserDetails(findUser.getId());

        // then
        assertThat(myPageResponseDto).isNotNull();
        assertThat(myPageResponseDto.getIdentification()).isEqualTo("abcd123");
        assertThat(myPageResponseDto.getName()).isEqualTo("민지홍");
        assertThat(myPageResponseDto.getBirthDate()).isEqualTo(LocalDate.parse("1999-12-30"));
        assertThat(myPageResponseDto.getPhoneNumber()).isEqualTo("01012341234");
        assertThat(myPageResponseDto.getDeliveryAddresses()).isNotNull();
        assertThat(myPageResponseDto.getDeliveryAddresses().size()).isEqualTo(2);
        assertThat(myPageResponseDto.getDeliveryAddresses().isEmpty()).isFalse(); // Assuming delivery addresses are not empty
    }
    @Test
    public void getUserDetails_UserNotFoundException() {
        // given
        Long invalidUserId = -1L;

        // when & then
        assertThrows(NotFoundException.class, () -> {
            userService.getUserDetails(invalidUserId);
        });
    }

    @Test
    @Transactional
    public void generateAccessToken() {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);
        Users findUser = userRepository.findByIdentification("abcd123").get();

        // when
        String accessToken = userService.generateAccessToken(findUser);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
    }


    @Test
    @Transactional
    public void generateRefreshToken() {
        // given
        SignUpDto signUpDto = new SignUpDto("abcd123", "abcd123!@#", "abcd123!@#",
                "민지홍", "1999-12-30", "01012341234");
        userService.signUpAccount(signUpDto);
        Users findUser = userRepository.findByIdentification("abcd123").get();

        // when
        String refreshToken = userService.generateRefreshToken(findUser);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
    }

    @Test
    @Transactional
    public void signUpAdminAccount_Success(){
        // given
        SignUpDto signUpDto = new SignUpDto("admin123","admin123!@#","admin123!@#",
                "민지홍","1999-12-30","01012341234");
        // when
        userService.signUpAdminAccount(signUpDto);
        Users findAdmin = userRepository.findByIdentification("admin123").get();
        // then
        assertThat(findAdmin.getId()).isNotNull();
        assertThat(findAdmin.getIdentification()).isEqualTo("admin123");
        assertThat(passwordEncoder.matches("admin123!@#", findAdmin.getPassword())).isTrue();
        assertThat(findAdmin.getName()).isEqualTo("민지홍");
        assertThat(findAdmin.getBirthDate()).isEqualTo(LocalDate.parse("1999-12-30"));
        assertThat(findAdmin.getPhoneNumber()).isEqualTo("01012341234");

        assertThat(findAdmin.getRole()).isEqualTo(ADMIN);

        assertThat(findAdmin.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(findAdmin.getCreationTime()).isNotNull();
        assertThat(findAdmin.getLastModifiedTime()).isNotNull();
    }
    @Test
    @Transactional
    public void signUpAdminAccount_DuplicateIdentificationException(){
        // given
        Users admin = Users.builder()
                .identification("admin123")
                .build();
        userRepository.save(admin);
        // when & then
        SignUpDto signUpDto = new SignUpDto("admin123","admin123!@#","admin123!@#",
                "민지홍","1999-12-30","01012341234");
        assertThrows(DuplicateNameException.class,()->{
            userService.signUpAdminAccount(signUpDto);
        });
    }
    @Test
    public void signUpAdminAccount_PasswordMismatchException(){
        // given
        SignUpDto signUpDto = new SignUpDto("admin123","admin123!@#","wrong123!@#",
                "민지홍","1999-12-30","01012341234");
        // when & then
        assertThrows(PasswordMismatchException.class,()->{
            userService.signUpAdminAccount(signUpDto);
        });

    }
    @Test
    @Transactional
    public void getUsers_Success() {
        // given
        SignUpDto signUpDto1 = new SignUpDto("user1", "password1!@#", "password1!@#",
                "사용자1", "1990-01-01", "01012345678");
        userService.signUpAccount(signUpDto1);

        SignUpDto signUpDto2 = new SignUpDto("user2", "password2!@#", "password2!@#",
                "사용자2", "1991-02-02", "01023456789");
        userService.signUpAccount(signUpDto2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<UserSummaryDto> userSummaries = userService.getUsers(pageable);

        // then
        assertThat(userSummaries).isNotNull();
        assertThat(userSummaries.getTotalElements()).isEqualTo(2);
        assertThat(userSummaries.getContent()).hasSize(2);

        UserSummaryDto user1 = userSummaries.getContent().get(0);
        assertThat(user1.getName()).isEqualTo("사용자1");

        UserSummaryDto user2 = userSummaries.getContent().get(1);
        assertThat(user2.getName()).isEqualTo("사용자2");
    }

}
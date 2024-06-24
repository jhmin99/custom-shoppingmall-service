package jihong99.shoppingmall.service;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.LoginDto;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static jihong99.shoppingmall.entity.enums.Roles.*;
import static jihong99.shoppingmall.entity.enums.Tiers.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){
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
        assertThrows(DuplicateIdentificationException.class,()->{
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
        DateTimeParseException dateTimeParseException = assertThrows(DateTimeParseException.class, () -> {
            userService.signUpAccount(signUpDto);
        });
        assertThat(dateTimeParseException.getMessage()).isEqualTo("Invalid birth date.");
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
        DuplicateIdentificationException duplicateIdentificationException = assertThrows(DuplicateIdentificationException.class, () -> {
            userService.checkDuplicateIdentification(signUpDto.getIdentification());
        });
        assertThat(duplicateIdentificationException.getMessage()).isEqualTo("The ID already exists.");
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

        LoginDto loginDto = new LoginDto("abcd123", "abcd123!@#");

        // when
        userService.loginByIdentificationAndPassword(loginDto);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getPrincipal()).isEqualTo("abcd123");
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

        LoginDto loginDto = new LoginDto(identification, password);

        // when & then
        assertThrows(BadCredentialsException.class, () -> {
            userService.loginByIdentificationAndPassword(loginDto);
        });
    }


}
package jihong99.shoppingmall.service;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.SignUpDto;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DuplicateIdentificationException;
import jihong99.shoppingmall.exception.PasswordMismatchException;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static jihong99.shoppingmall.entity.enums.Tier.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImplTest.class);
    @BeforeEach
    public void setUp(){

    }

    @AfterEach
    public void tearDown(){
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
        assertThat(findUser.getPassword()).isEqualTo("abcd123!@#");
        assertThat(findUser.getName()).isEqualTo("민지홍");
        assertThat(findUser.getBirthDate()).isEqualTo(LocalDate.parse("1999-12-30"));
        assertThat(findUser.getPhoneNumber()).isEqualTo("01012341234");

        assertThat(findUser.getPoint()).isEqualTo(0);
        assertThat(findUser.getTier()).isEqualTo(IRON);
        assertThat(findUser.getAmountToNextTier()).isEqualTo(50000);

        assertThat(findUser.getCart()).isNotNull();
        assertThat(findUser.getCart().getEstimatedTotalPrice()).isEqualTo(0L);
        assertThat(findUser.getWishList()).isNotNull();

        assertThat(findUser.getRegistrationDate()).isEqualTo(LocalDate.now());
        assertThat(findUser.getCreationTime()).isNotNull();
        assertThat(findUser.getLastModifiedTime()).isNotNull();

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
        assertThat(passwordMismatchException.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
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
        assertThat(dateTimeParseException.getMessage()).isEqualTo("생년월일이 올바르지 않습니다.");
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
        assertThat(duplicateIdentificationException.getMessage()).isEqualTo("중복된 아이디가 존재합니다.");
    }

}
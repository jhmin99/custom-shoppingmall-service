package jihong99.shoppingmall.entity;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class UsersTest {

    @Autowired
    UserRepository userRepository;
    Logger LOGGER = LoggerFactory.getLogger(UsersTest.class);
    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }
    @Test
    public void userBuilderTest(){
        // given
        String birthDate = "1999-12-30";
        Users users = Users.builder()
                .identification("abc1233")
                .password("abcd1233!@")
                .name("민지홍")
                .birthDate(LocalDate.parse(birthDate))
                .phoneNumber("01012341234")
                .build();
        // when
        Users savedUser = userRepository.save(users);
        // then
        assertThat(savedUser.getIdentification()).isEqualTo("abc1233");
        assertThat(savedUser.getPassword()).isEqualTo("abcd1233!@");
        assertThat(savedUser.getName()).isEqualTo("민지홍");
        assertThat(savedUser.getBirthDate().getYear()).isEqualTo(1999);
        assertThat(savedUser.getBirthDate().getMonthValue()).isEqualTo(12);
        assertThat(savedUser.getBirthDate().getDayOfMonth()).isEqualTo(30);
        assertThat(savedUser.getPhoneNumber()).isEqualTo("01012341234");

    }

    @Test
    public void userCreationTest(){
        // given
        String birthDate = "1999-12-30";
        Users users = Users.builder()
                .identification("abc1233")
                .password("abcd1233!@")
                .name("민지홍")
                .birthDate(LocalDate.parse(birthDate))
                .phoneNumber("01012341234")
                .build();
        // when
        Users savedUser = userRepository.save(users);
        // then
        assertThat(savedUser.getId()).isEqualTo(2L);
        assertThat(savedUser.getRegistrationDate()).isNotNull();
        LOGGER.info(savedUser.getRegistrationDate().toString());

        assertThat(savedUser.getCreationTime()).isNotNull();
        LOGGER.info(savedUser.getCreationTime().toString());

        assertThat(savedUser.getLastModifiedTime()).isNotNull();
        LOGGER.info(savedUser.getLastModifiedTime().toString());

    }

}
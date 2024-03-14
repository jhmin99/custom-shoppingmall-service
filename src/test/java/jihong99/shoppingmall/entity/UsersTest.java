package jihong99.shoppingmall.entity;
import jakarta.transaction.Transactional;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import static jihong99.shoppingmall.entity.enums.Tier.*;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class UsersTest {
    @Autowired
    private UserRepository userRepository;
    private Users users;
    private final Logger LOGGER = LoggerFactory.getLogger(UsersTest.class);
    @BeforeEach
    public void setUp() {
        String birthDate = "1999-12-30";
        users =  Users.builder()
                .identification("abc1233")
                .password("abcd1233!@")
                .name("민지홍")
                .birthDate(LocalDate.parse(birthDate))
                .phoneNumber("01012341234")
                .build();

    }
    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }
    /**
     * Test for creating a user using the builder pattern
     */
    @Test
    public void userBuilder_Success(){
        // given
        // when
        // then
        assertThat(users.getId()).isNull();
        assertThat(users.getIdentification()).isEqualTo("abc1233");
        assertThat(users.getPassword()).isEqualTo("abcd1233!@");
        assertThat(users.getName()).isEqualTo("민지홍");
        assertThat(users.getBirthDate().getYear()).isEqualTo(1999);
        assertThat(users.getBirthDate().getMonthValue()).isEqualTo(12);
        assertThat(users.getBirthDate().getDayOfMonth()).isEqualTo(30);
        assertThat(users.getPhoneNumber()).isEqualTo("01012341234");

        LOGGER.info(Users.builder()
                .identification("abc1233")
                .password("abcd1233!@")
                .name("민지홍")
                .birthDate(LocalDate.parse("1999-12-30"))
                .phoneNumber("01012341234")
                .build().toString());
    }

    /**
     * Test for creating a user and saving it using jpa
     */
    @Test
    public void userCreation_Success(){
        // given
        // when
        Users savedUser = userRepository.save(users);
        // then
        assertThat(savedUser.getRegistrationDate()).isEqualTo(LocalDate.now());
        LOGGER.info(savedUser.getRegistrationDate().toString());

        assertThat(savedUser.getCreationTime()).isNotNull();
        LOGGER.info(savedUser.getCreationTime().toString());

        assertThat(savedUser.getLastModifiedTime()).isNotNull();
        LOGGER.info(savedUser.getLastModifiedTime().toString());
    }

    /**
     * Test for updating cart method
     */
    @Test
    public void updateCart_Success(){
        // given
        Cart cart = new Cart(0L);
        // when
        users.updateCart(cart);
        // then
        assertThat(users.getCart()).isEqualTo(cart);
        assertThat(users.getCart().getEstimatedTotalPrice()).isEqualTo(0L);
    }

    /**
     * Test for updating wish list method
     */
    @Test
    public void updateWishList_Success(){
        // given
        WishList wishList = new WishList();
        // when
        users.updateWishList(wishList);
        // then
        assertThat(users.getWishList()).isEqualTo(wishList);
    }

    /**
     * Test for updating phone number method
     */
    @Test
    public void updatePhoneNumber_Success(){
        // given
        // when
        users.updatePhoneNumber("123412340000");
        // then
        assertThat(users.getPhoneNumber()).isEqualTo("123412340000");
    }

    /**
     * Test for updating point method
     */
    @Test
    public void updatePoint_Success(){
        // given
        // when
        users.updatePoint(0);
        // then
        assertThat(users.getPoint()).isEqualTo(0);
    }

    /**
     * Test for updating tier method
     */
    @Test
    public void updateTier_Success(){
        // given
        // when
        users.updateTier(IRON);
        // then
        assertThat(users.getTier()).isEqualTo(IRON);
    }

    /**
     * Test for updating amount to next tier test
     */
    @Test
    public void updateAmountToNextTier_Success(){
        // given
        // when
        users.updateAmountToNextTier(50000);
        // then
        assertThat(users.getAmountToNextTier()).isEqualTo(50000);
    }

}
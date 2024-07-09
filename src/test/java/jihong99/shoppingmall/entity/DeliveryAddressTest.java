package jihong99.shoppingmall.entity;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DeliveryAddressTest {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private UserRepository userRepository;


    private DeliveryAddress deliveryAddress;
    private Users user;

    private final Logger LOGGER = LoggerFactory.getLogger(DeliveryAddressTest.class);

    @BeforeEach
    public void setUp() {
        String birthDate = "1999-12-30";
        user = Users.builder()
                .identification("abc1233")
                .password("abcd1233!@")
                .name("민지홍")
                .birthDate(LocalDate.parse(birthDate))
                .phoneNumber("01012341234")
                .build();
        userRepository.save(user);

        deliveryAddress = DeliveryAddress.builder()
                .users(user)
                .name("Test Name")
                .phoneNumber("01012345678")
                .zipCode(12345)
                .address("Test Address")
                .addressDetail("Test Address Detail")
                .build();
    }

    @AfterEach
    public void tearDown() {
        deliveryAddressRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Test for creating a delivery address using the builder pattern
     */
    @Test
    public void deliveryAddressBuilder_Success() {
        // given
        // when
        // then
        assertThat(deliveryAddress.getId()).isNull();
        assertThat(deliveryAddress.getUsers()).isEqualTo(user);
        assertThat(deliveryAddress.getName()).isEqualTo("Test Name");
        assertThat(deliveryAddress.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(deliveryAddress.getZipCode()).isEqualTo(12345);
        assertThat(deliveryAddress.getAddress()).isEqualTo("Test Address");
        assertThat(deliveryAddress.getAddressDetail()).isEqualTo("Test Address Detail");

        LOGGER.info(deliveryAddress.toString());
    }

    /**
     * Test for creating and saving a delivery address using JPA
     */
    @Test
    public void deliveryAddressCreation_Success() {
        // given
        // when
        DeliveryAddress savedAddress = deliveryAddressRepository.save(deliveryAddress);
        // then
        assertThat(savedAddress.getId()).isNotNull();
        assertThat(savedAddress.getUsers()).isEqualTo(user);
        assertThat(savedAddress.getName()).isEqualTo("Test Name");
        assertThat(savedAddress.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(savedAddress.getZipCode()).isEqualTo(12345);
        assertThat(savedAddress.getAddress()).isEqualTo("Test Address");
        assertThat(savedAddress.getAddressDetail()).isEqualTo("Test Address Detail");

        LOGGER.info(savedAddress.toString());
    }

    /**
     * Test for updating the name field
     */
    @Test
    public void updateName_Success() {
        // given
        // when
        deliveryAddress.updateName("New Test Name");
        // then
        assertThat(deliveryAddress.getName()).isEqualTo("New Test Name");
    }

    /**
     * Test for updating the phone number field
     */
    @Test
    public void updatePhoneNumber_Success() {
        // given
        // when
        deliveryAddress.updatePhoneNumber("01098765432");
        // then
        assertThat(deliveryAddress.getPhoneNumber()).isEqualTo("01098765432");
    }

    /**
     * Test for updating the zip code field
     */
    @Test
    public void updateZipCode_Success() {
        // given
        // when
        deliveryAddress.updateZipCode(54321);
        // then
        assertThat(deliveryAddress.getZipCode()).isEqualTo(54321);
    }

    /**
     * Test for updating the address field
     */
    @Test
    public void updateAddress_Success() {
        // given
        // when
        deliveryAddress.updateAddress("New Test Address");
        // then
        assertThat(deliveryAddress.getAddress()).isEqualTo("New Test Address");
    }

    /**
     * Test for updating the address detail field
     */
    @Test
    public void updateAddressDetail_Success() {
        // given
        // when
        deliveryAddress.updateAddressDetail("New Test Address Detail");
        // then
        assertThat(deliveryAddress.getAddressDetail()).isEqualTo("New Test Address Detail");
    }
}

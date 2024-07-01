package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DeliveryAddressNotFoundException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class DeliveryAddressServiceImplTest {

    @Autowired
    private IDeliveryAddressService deliveryAddressService;
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        deliveryAddressRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * Tests the successful retrieval of delivery addresses.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void getDeliveryAddresses_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        DeliveryAddress address = DeliveryAddress.builder()
                .users(user)
                .address("Test Address")
                .addressDetail("Test Detail")
                .zipCode(12345)
                .name("Test Name")
                .phoneNumber("01012345678")
                .build();
        deliveryAddressRepository.save(address);

        // when
        Set<DeliveryAddress> addresses = deliveryAddressService.getDeliveryAddresses(user);

        // then
        assertThat(addresses).isNotEmpty();
        assertThat(addresses.size()).isEqualTo(1);
        assertThat(addresses.iterator().next().getAddress()).isEqualTo("Test Address");
    }

    /**
     * Tests the retrieval of delivery addresses when there are none.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void getDeliveryAddresses_Without_Address_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        // when
        Set<DeliveryAddress> addresses = deliveryAddressService.getDeliveryAddresses(user);

        // then
        assertThat(addresses).isEmpty();
    }

    /**
     * Tests the successful addition of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void addDeliveryAddress_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        DeliveryAddressDto dto = new DeliveryAddressDto(user.getId(), null, "Test Name", "01012345678", 12345, "Test Address", "Test Detail");

        // when
        deliveryAddressService.addDeliveryAddress(dto);
        Set<DeliveryAddress> addresses = deliveryAddressService.getDeliveryAddresses(user);

        // then
        assertThat(addresses).isNotEmpty();
        assertThat(addresses.size()).isEqualTo(1);
        assertThat(addresses.iterator().next().getAddress()).isEqualTo("Test Address");
    }

    /**
     * Tests handling of a UserNotFoundException when adding a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void addDeliveryAddress_UserNotFoundException() {
        // given
        DeliveryAddressDto dto = new DeliveryAddressDto(-1L, null, "Test Name", "01012345678", 12345, "Test Address", "Test Detail");

        // when & then
        assertThrows(NotFoundException.class, () -> {
            deliveryAddressService.addDeliveryAddress(dto);
        });
    }

    /**
     * Tests the successful update of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void updateDeliveryAddress_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        DeliveryAddress address = DeliveryAddress.builder()
                .users(user)
                .address("Test Address")
                .addressDetail("Test Detail")
                .zipCode(12345)
                .name("Test Name")
                .phoneNumber("01012345678")
                .build();
        deliveryAddressRepository.save(address);

        DeliveryAddressDto dto = new DeliveryAddressDto(user.getId(), address.getId(), "Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");

        // when
        deliveryAddressService.updateDeliveryAddress(address.getId(), dto);
        DeliveryAddress updatedAddress = deliveryAddressRepository.findById(address.getId()).orElse(null);

        // then
        assertThat(updatedAddress).isNotNull();
        assertThat(updatedAddress.getName()).isEqualTo("Updated Name");
        assertThat(updatedAddress.getPhoneNumber()).isEqualTo("01087654321");
        assertThat(updatedAddress.getZipCode()).isEqualTo(54321);
        assertThat(updatedAddress.getAddress()).isEqualTo("Updated Address");
        assertThat(updatedAddress.getAddressDetail()).isEqualTo("Updated Detail");
    }

    /**
     * Tests handling of a DeliveryAddressNotFoundException when updating a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void updateDeliveryAddress_DeliveryAddressNotFoundException() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        DeliveryAddressDto dto = new DeliveryAddressDto(user.getId(), -1L, "Updated Name", "01087654321", 54321, "Updated Address", "Updated Detail");

        // when & then
        assertThrows(DeliveryAddressNotFoundException.class, () -> {
            deliveryAddressService.updateDeliveryAddress(-1L, dto);
        });
    }

    /**
     * Tests the successful deletion of a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void deleteDeliveryAddress_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();
        userRepository.save(user);

        DeliveryAddress address = DeliveryAddress.builder()
                .users(user)
                .address("Test Address")
                .addressDetail("Test Detail")
                .zipCode(12345)
                .name("Test Name")
                .phoneNumber("01012345678")
                .build();
        deliveryAddressRepository.save(address);

        // when
        deliveryAddressService.deleteDeliveryAddress(address.getId());

        // then
        assertThrows(DeliveryAddressNotFoundException.class, () -> {
            deliveryAddressService.deleteDeliveryAddress(address.getId());
        });
        assertThat(deliveryAddressRepository.findById(address.getId()).isEmpty()).isTrue();
    }

    /**
     * Tests handling of a DeliveryAddressNotFoundException when deleting a delivery address.
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @Transactional
    void deleteDeliveryAddress_DeliveryAddressNotFoundException() {
        // when & then
        assertThrows(DeliveryAddressNotFoundException.class, () -> {
            deliveryAddressService.deleteDeliveryAddress(-1L);
        });
    }
}

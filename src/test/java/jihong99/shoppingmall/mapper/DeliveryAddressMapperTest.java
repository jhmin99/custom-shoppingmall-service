package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ActiveProfiles("test")
class DeliveryAddressMapperTest {

    /**
     * Tests the mapping of DeliveryAddressDto to DeliveryAddress entity.
     */
    @Test
    void mapToDeliveryAddress_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();

        DeliveryAddressDto dto = new DeliveryAddressDto(
                user.getId(),
                null,
                "Test Name",
                "01012345678",
                12345,
                "Test Address",
                "Test Detail"
        );

        // when
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        DeliveryAddress address = mapper.mapToDeliveryAddress(user, dto);

        // then
        assertThat(address.getUsers()).isEqualTo(user);
        assertThat(address.getName()).isEqualTo("Test Name");
        assertThat(address.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(address.getZipCode()).isEqualTo(12345);
        assertThat(address.getAddress()).isEqualTo("Test Address");
        assertThat(address.getAddressDetail()).isEqualTo("Test Detail");
    }

    /**
     * Tests the mapping of DeliveryAddress entity to DeliveryAddressDto.
     */
    @Test
    void mapToDeliveryAddressDto_Success() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();

        DeliveryAddress address = DeliveryAddress.builder()
                .id(1L)
                .users(user)
                .name("Test Name")
                .phoneNumber("01012345678")
                .zipCode(12345)
                .address("Test Address")
                .addressDetail("Test Detail")
                .build();

        // when
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        DeliveryAddressDto dto = mapper.mapToDeliveryAddressDto(user, address);

        // then
        assertThat(dto.getUserId()).isEqualTo(user.getId());
        assertThat(dto.getId()).isEqualTo(address.getId());
        assertThat(dto.getName()).isEqualTo("Test Name");
        assertThat(dto.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(dto.getZipCode()).isEqualTo(12345);
        assertThat(dto.getAddress()).isEqualTo("Test Address");
        assertThat(dto.getAddressDetail()).isEqualTo("Test Detail");
    }

    /**
     * Tests the scenario where DeliveryAddressDto is null.
     */
    @Test
    void mapToDeliveryAddress_NullDto() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();

        // when & then
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.mapToDeliveryAddress(user, null));
    }

    /**
     * Tests the scenario where Users entity is null.
     */
    @Test
    void mapToDeliveryAddress_NullUser() {
        // given
        DeliveryAddressDto dto = new DeliveryAddressDto(
                1L,
                null,
                "Test Name",
                "01012345678",
                12345,
                "Test Address",
                "Test Detail"
        );

        // when & then
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.mapToDeliveryAddress(null, dto));
    }

    /**
     * Tests the scenario where DeliveryAddress is null.
     */
    @Test
    void mapToDeliveryAddressDto_NullAddress() {
        // given
        Users user = Users.builder()
                .identification("testuser")
                .build();

        // when & then
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.mapToDeliveryAddressDto(user, null));
    }

    /**
     * Tests the scenario where Users entity is null for DTO mapping.
     */
    @Test
    void mapToDeliveryAddressDto_NullUser() {
        // given
        DeliveryAddress address = DeliveryAddress.builder()
                .id(1L)
                .name("Test Name")
                .phoneNumber("01012345678")
                .zipCode(12345)
                .address("Test Address")
                .addressDetail("Test Detail")
                .build();

        // when & then
        DeliveryAddressMapper mapper = new DeliveryAddressMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.mapToDeliveryAddressDto(null, address));
    }
}

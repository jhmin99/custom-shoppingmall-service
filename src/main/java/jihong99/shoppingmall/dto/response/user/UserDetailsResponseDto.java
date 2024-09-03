package jihong99.shoppingmall.dto.response.user;

import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;



@Getter
@AllArgsConstructor
public class UserDetailsResponseDto {
    private Long id;
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Set<Address> deliveryAddresses;
    private Timestamp creationTime;
    private Timestamp lastModifiedTime;

    @Getter
    @AllArgsConstructor
    public static class Address {
        private Long addressId;
        private Integer zipCode;
        private String address;
        private String addressDetail;
    }

    public static UserDetailsResponseDto of(Users findUser, Set<DeliveryAddress> deliveryAddresses) {
        Set<Address> deliveryAddressDtos = getAddresses(deliveryAddresses);
        return new UserDetailsResponseDto(findUser.getId(), findUser.getIdentification(), findUser.getName(), findUser.getBirthDate(), findUser.getPhoneNumber(), deliveryAddressDtos, findUser.getCreationTime(), findUser.getLastModifiedTime());
    }

    private static Set<Address> getAddresses(Set<DeliveryAddress> deliveryAddresses) {
        return deliveryAddresses.stream()
                .map(address -> new Address(
                        address.getId(),
                        address.getZipCode(),
                        address.getAddress(),
                        address.getAddressDetail()
                ))
                .collect(Collectors.toSet());
    }

}

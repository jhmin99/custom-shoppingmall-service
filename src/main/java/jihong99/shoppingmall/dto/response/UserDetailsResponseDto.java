package jihong99.shoppingmall.dto.response;

import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;



@Getter
@AllArgsConstructor
public class UserDetailsResponseDto {
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Set<Address> deliveryAddresses;

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
        return new UserDetailsResponseDto(findUser.getIdentification(), findUser.getName(), findUser.getBirthDate(), findUser.getPhoneNumber(), deliveryAddressDtos);
    }

    private static Set<Address> getAddresses(Set<DeliveryAddress> deliveryAddresses) {
        Set<Address> deliveryAddressDtos = deliveryAddresses.stream()
                .map(address -> new Address(
                        address.getId(),
                        address.getZipCode(),
                        address.getAddress(),
                        address.getAddressDetail()
                ))
                .collect(Collectors.toSet());
        return deliveryAddressDtos;
    }

}

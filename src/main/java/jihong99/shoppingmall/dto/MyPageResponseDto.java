package jihong99.shoppingmall.dto;

import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.mapper.DeliveryAddressMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;


@Getter
@AllArgsConstructor
public class MyPageResponseDto {
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Set<DeliveryAddressDto> deliveryAddresses;

    public static MyPageResponseDto of(Users findUser, Set<DeliveryAddress> deliveryAddresses) {
        DeliveryAddressMapper deliveryAddressMapper = new DeliveryAddressMapper();
        Set<DeliveryAddressDto> deliveryAddressDto = deliveryAddresses.stream()
                .map(address -> deliveryAddressMapper.mapToDeliveryAddressDto(findUser, address))
                .collect(Collectors.toSet());
        return new MyPageResponseDto(findUser.getIdentification(), findUser.getName(), findUser.getBirthDate(), findUser.getPhoneNumber(), deliveryAddressDto);
    }

}

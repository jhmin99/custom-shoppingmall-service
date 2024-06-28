package jihong99.shoppingmall.dto;

import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.mapper.DeliveryAddressMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.UserConstants.*;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponseDto {
    private String statusCode;
    private String statusMessage;
    private String identification;
    private String name;
    private LocalDate birthDate;
    private String phoneNumber;
    private Set<DeliveryAddressDto> deliveryAddresses;

    /**
     * Creates a successful MyPageResponseDto.
     *
     * @param findUser The user whose details are to be included in the response.
     * @param deliveryAddresses The set of delivery addresses associated with the user.
     * @return MyPageResponseDto A response object containing user details and delivery addresses.
     */
    public static MyPageResponseDto success(Users findUser, Set<DeliveryAddress> deliveryAddresses) {
        DeliveryAddressMapper deliveryAddressMapper = new DeliveryAddressMapper();
        Set<DeliveryAddressDto> deliveryAddressDto = deliveryAddresses.stream()
                .map(address -> deliveryAddressMapper.mapToDeliveryAddressDto(findUser, address))
                .collect(Collectors.toSet());
        return new MyPageResponseDto(STATUS_200, MESSAGE_200_fetchSuccess, findUser.getIdentification(), findUser.getName(), findUser.getBirthDate(), findUser.getPhoneNumber(), deliveryAddressDto);
    }

    /**
     * Creates an error MyPageResponseDto.
     *
     * @param statusCode The status code of the error.
     * @param statusMessage The status message of the error.
     * @return MyPageResponseDto A response object containing error details.
     */
    public static MyPageResponseDto error(String statusCode, String statusMessage) {
        return new MyPageResponseDto(statusCode, statusMessage, null, null, null, null, null);
    }
}

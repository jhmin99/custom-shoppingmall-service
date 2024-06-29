package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;

public class DeliveryAddressMapper {

    public DeliveryAddress mapToDeliveryAddress(Users findUser, DeliveryAddressDto requestDto){
        if (findUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (requestDto == null) {
            throw new IllegalArgumentException("DeliveryAddressDto cannot be null");
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .users(findUser)
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .zipCode(requestDto.getZipCode())
                .address(requestDto.getAddress())
                .addressDetail(requestDto.getAddressDetail())
                .build();
        return deliveryAddress;
    }

    public DeliveryAddressDto mapToDeliveryAddressDto(Users findUser, DeliveryAddress deliveryAddress){
        if (findUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (deliveryAddress == null) {
            throw new IllegalArgumentException("DeliveryAddress cannot be null");
        }

        DeliveryAddressDto deliveryAddressDto = new DeliveryAddressDto(
                findUser.getId(),
                deliveryAddress.getId(),
                deliveryAddress.getName(),
                deliveryAddress.getPhoneNumber(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getAddress(),
                deliveryAddress.getAddressDetail()
        );
        return deliveryAddressDto;
    }
}

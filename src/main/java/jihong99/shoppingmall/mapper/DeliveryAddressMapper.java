package jihong99.shoppingmall.mapper;

import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;


public class DeliveryAddressMapper {

    public DeliveryAddress mapToDeliveryAddress(Users findUser, DeliveryAddressDto requestDto){
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
        DeliveryAddressDto deliveryAddressDto = new DeliveryAddressDto(findUser.getId(), deliveryAddress.getId(), deliveryAddress.getName(), deliveryAddress.getPhoneNumber(), deliveryAddress.getZipCode(), deliveryAddress.getAddress(), deliveryAddress.getAddressDetail());
        return  deliveryAddressDto;
    }
}

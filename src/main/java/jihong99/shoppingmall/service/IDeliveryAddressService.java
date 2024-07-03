package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;

import java.util.Set;

public interface IDeliveryAddressService {
    Set<DeliveryAddress> getDeliveryAddresses(Users findUser);

    Long addDeliveryAddress(DeliveryAddressDto requestDto);

    void updateDeliveryAddress(Long addressId, DeliveryAddressDto requestDto);

    void deleteDeliveryAddress(Long addressId);
}

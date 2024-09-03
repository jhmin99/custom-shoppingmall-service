package jihong99.shoppingmall.service;


import jihong99.shoppingmall.dto.request.deliveryAddress.DeliveryAddressRequestDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;

import java.util.Set;

public interface IDeliveryAddressService {

    void addDeliveryAddress(Long userId, DeliveryAddressRequestDto deliveryAddressRequestDto);

    void updateDeliveryAddress(Long userId, Long deliveryAddressId, DeliveryAddressRequestDto deliveryAddressRequestDto);

    void deleteDeliveryAddress(Long userId, Long deliveryAddressId);

    Set<DeliveryAddress> findDeliveryAddressesByUser(Users user);
}

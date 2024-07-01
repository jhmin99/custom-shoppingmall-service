package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.DeliveryAddressNotFoundException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.mapper.DeliveryAddressMapper;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class DeliveryAddressServiceImpl implements IDeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    /**
     * Retrieves a set of delivery addresses for the specified user.
     *
     * @param findUser The user for whom the delivery addresses are to be retrieved.
     * @return A set of delivery addresses associated with the user.
     */
    @Override
    public Set<DeliveryAddress> getDeliveryAddresses(Users findUser) {
        return deliveryAddressRepository.findByUsersId(findUser.getId());
    }

    /**
     * Adds a new delivery address for the specified user.
     *
     * @param requestDto The DTO object containing the delivery address details.
     * @throws NotFoundException If the user with the specified ID is not found.
     */
    @Override
    @Transactional
    public void addDeliveryAddress(DeliveryAddressDto requestDto) {
        Users findUser = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + requestDto.getUserId()));
        DeliveryAddressMapper deliveryAddressMapper = new DeliveryAddressMapper();
        DeliveryAddress deliveryAddress = deliveryAddressMapper.mapToDeliveryAddress(findUser, requestDto);
        deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * Updates an existing delivery address with the specified details.
     *
     * @param addressId The ID of the delivery address to be updated.
     * @param requestDto The DTO object containing the updated delivery address details.
     * @throws DeliveryAddressNotFoundException If the delivery address with the specified ID is not found.
     */
    @Override
    @Transactional
    public void updateDeliveryAddress(Long addressId, DeliveryAddressDto requestDto) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with id: " + addressId));

        deliveryAddress.updateAddress(requestDto.getAddress());
        deliveryAddress.updateAddressDetail(requestDto.getAddressDetail());
        deliveryAddress.updateZipCode(requestDto.getZipCode());
        deliveryAddress.updateName(requestDto.getName());
        deliveryAddress.updatePhoneNumber(requestDto.getPhoneNumber());

        deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * Deletes the delivery address with the specified ID.
     *
     * @param addressId The ID of the delivery address to be deleted.
     * @throws DeliveryAddressNotFoundException If the delivery address with the specified ID is not found.
     */
    @Override
    @Transactional
    public void deleteDeliveryAddress(Long addressId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("Delivery address not found with id: " + addressId));
        deliveryAddressRepository.delete(deliveryAddress);
    }
}

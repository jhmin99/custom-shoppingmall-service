package jihong99.shoppingmall.service;

import jakarta.transaction.Transactional;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.mapper.DeliveryAddressMapper;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static jihong99.shoppingmall.constants.Constants.*;


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
     * @return Id of saved delivery address
     */
    @Override
    @Transactional
    public Long addDeliveryAddress(DeliveryAddressDto requestDto) {
        Users findUser = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
        DeliveryAddressMapper deliveryAddressMapper = new DeliveryAddressMapper();
        DeliveryAddress deliveryAddress = deliveryAddressMapper.mapToDeliveryAddress(findUser, requestDto);
        return deliveryAddressRepository.save(deliveryAddress).getId();
    }

    /**
     * Updates an existing delivery address with the specified details.
     *
     * @param addressId The ID of the delivery address to be updated.
     * @param requestDto The DTO object containing the updated delivery address details.
     * @throws NotFoundException If the delivery address with the specified ID is not found.
     */
    @Override
    @Transactional
    public void updateDeliveryAddress(Long addressId, DeliveryAddressDto requestDto) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_DeliveryAddressNotFound));
        applyAddressUpdates(requestDto, deliveryAddress);
        deliveryAddressRepository.save(deliveryAddress);
    }

    private static void applyAddressUpdates(DeliveryAddressDto requestDto, DeliveryAddress deliveryAddress) {
        deliveryAddress.updateAddress(requestDto.getAddress());
        deliveryAddress.updateAddressDetail(requestDto.getAddressDetail());
        deliveryAddress.updateZipCode(requestDto.getZipCode());
        deliveryAddress.updateName(requestDto.getName());
        deliveryAddress.updatePhoneNumber(requestDto.getPhoneNumber());
    }

    /**
     * Deletes the delivery address with the specified ID.
     *
     * @param addressId The ID of the delivery address to be deleted.
     * @throws NotFoundException If the delivery address with the specified ID is not found.
     */
    @Override
    @Transactional
    public void deleteDeliveryAddress(Long addressId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_DeliveryAddressNotFound));
        deliveryAddressRepository.delete(deliveryAddress);
    }
}

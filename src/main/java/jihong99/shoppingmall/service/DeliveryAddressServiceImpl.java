package jihong99.shoppingmall.service;

import jihong99.shoppingmall.dto.request.deliveryAddress.DeliveryAddressRequestDto;
import jihong99.shoppingmall.entity.DeliveryAddress;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.DeliveryAddressRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static jihong99.shoppingmall.constants.Constants.*;


@Service
@RequiredArgsConstructor
public class DeliveryAddressServiceImpl implements IDeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;


    /**
     * Adds a new delivery address for a specific user.
     *
     * @param userId The ID of the user to whom the address will be added.
     * @param requestDto The DTO containing the address details.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    @Transactional
    public void addDeliveryAddress(Long userId, DeliveryAddressRequestDto requestDto) {
        Users findUser = findUserOrThrow(userId);
        DeliveryAddress deliveryAddress = getDeliveryAddress(requestDto, findUser);
        deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * Updates an existing delivery address for a specific user.
     *
     * @param userId The ID of the user.
     * @param addressId The ID of the address to update.
     * @param requestDto The DTO containing the updated address details.
     * @throws NotFoundException if the user or address is not found.
     */
    @Override
    @Transactional
    public void updateDeliveryAddress(Long userId, Long addressId, DeliveryAddressRequestDto requestDto) {
        findUserOrThrow(userId);
        DeliveryAddress deliveryAddress = findDeliveryAddressOrThrow(addressId);
        updateDeliveryAddress(requestDto, deliveryAddress);
        deliveryAddressRepository.save(deliveryAddress);
    }

    /**
     * Deletes a delivery address for a specific user.
     *
     * @param userId The ID of the user.
     * @param addressId The ID of the address to delete.
     * @throws NotFoundException if the user or address is not found.
     */
    @Override
    @Transactional
    public void deleteDeliveryAddress(Long userId, Long addressId) {
        findUserOrThrow(userId);
        DeliveryAddress deliveryAddress = findDeliveryAddressOrThrow(addressId);
        deliveryAddressRepository.delete(deliveryAddress);
    }

    /**
     * Retrieves all delivery addresses associated with a specific user.
     *
     * @param findUser The user whose delivery addresses are being retrieved.
     * @return A set of delivery addresses associated with the user.
     */
    @Override
    public Set<DeliveryAddress> findDeliveryAddressesByUser(Users findUser) {
        return deliveryAddressRepository.findAllByUsersId(findUser.getId());
    }


    private void updateDeliveryAddress(DeliveryAddressRequestDto requestDto, DeliveryAddress deliveryAddress) {
        deliveryAddress.updateAddress(requestDto.getAddress());
        deliveryAddress.updateAddressDetail(requestDto.getAddressDetail());
        deliveryAddress.updateZipCode(requestDto.getZipCode());
        deliveryAddress.updateName(requestDto.getName());
        deliveryAddress.updatePhoneNumber(requestDto.getPhoneNumber());
    }


    private DeliveryAddress findDeliveryAddressOrThrow(Long addressId) {
        return deliveryAddressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_DeliveryAddressNotFound));
    }

    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MESSAGE_404_UserNotFound));
    }


    private static DeliveryAddress getDeliveryAddress(DeliveryAddressRequestDto requestDto, Users findUser) {
        return DeliveryAddress.createDeliveryAddress(
                findUser,
                requestDto.getName(),
                requestDto.getPhoneNumber(),
                requestDto.getZipCode(),
                requestDto.getAddress(),
                requestDto.getAddressDetail());
    }
}

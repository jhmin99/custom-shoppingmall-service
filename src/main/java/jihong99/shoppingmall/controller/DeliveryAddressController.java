package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.deliveryAddress.DeliveryAddressRequestDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IDeliveryAddressService;
import jihong99.shoppingmall.utils.annotation.HasId;
import lombok.RequiredArgsConstructor;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryAddressController {
    private final IDeliveryAddressService ideliveryAddressService;

    /**
     * Adds a new delivery address for the specified user.
     *
     * @param userId The ID of the user to whom the address will be added.
     * @param deliveryAddressRequestDto The DTO containing the address details.
     * @return A ResponseEntity with status 201 (Created) and a message indicating successful creation.
     *
     * @success Response successfully created.
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields).
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (userId) cannot be converted to the expected type.
     * Response Code: 400
     *
     * @throws NotFoundException if the user with the specified ID is not found.
     * Response Code: 404
     *
     * @throws AccessDeniedException if the user does not have the required role.
     * Response Code: 403
     *
     * @throws Exception if any other internal server error occurs.
     * Response Code: 500
     */
    @HasId
    @PostMapping("/{userId}/deliveryAddresses")
    public ResponseEntity<ResponseDto> addDeliveryAddress(
            @PathVariable Long userId,
            @Valid @RequestBody DeliveryAddressRequestDto deliveryAddressRequestDto) {
        ideliveryAddressService.addDeliveryAddress(userId, deliveryAddressRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createDeliveryAddress));
    }

    /**
     * Updates an existing delivery address for the specified user.
     *
     * @param userId The ID of the user.
     * @param deliveryAddressId The ID of the delivery address to update.
     * @param deliveryAddressRequestDto The DTO containing the updated address details.
     * @return A ResponseEntity with status 200 (OK) and a message indicating successful update.
     *
     * @success Response successfully updated.
     * Response Code: 200
     *
     * @throws MethodArgumentNotValidException if the request data is invalid (e.g., missing required fields).
     * Response Code: 400
     *
     * @throws TypeMismatchException if the method argument (userId or deliveryAddressId) cannot be converted to the expected type.
     * Response Code: 400
     *
     * @throws NotFoundException if the user or delivery address with the specified ID is not found.
     * Response Code: 404
     *
     * @throws AccessDeniedException if the user does not have the required role.
     * Response Code: 403
     *
     * @throws Exception if any other internal server error occurs.
     * Response Code: 500
     */
    @HasId
    @PutMapping("/{userId}/deliveryAddresses/{deliveryAddressId}")
    public ResponseEntity<ResponseDto> updateDeliveryAddress(
            @PathVariable Long userId,
            @PathVariable Long deliveryAddressId,
            @Valid @RequestBody DeliveryAddressRequestDto deliveryAddressRequestDto) {
        ideliveryAddressService.updateDeliveryAddress(userId, deliveryAddressId, deliveryAddressRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateDeliveryAddressSuccess));
    }

    /**
     * Deletes an existing delivery address for the specified user.
     *
     * @param userId The ID of the user.
     * @param deliveryAddressId The ID of the delivery address to delete.
     * @return A ResponseEntity with status 200 (OK) and a message indicating successful deletion.
     *
     * @success Response successfully deleted.
     * Response Code: 200
     *
     * @throws TypeMismatchException if the method argument (userId or deliveryAddressId) cannot be converted to the expected type.
     * Response Code: 400
     *
     * @throws NotFoundException if the user or delivery address with the specified ID is not found.
     * Response Code: 404
     *
     * @throws AccessDeniedException if the user does not have the required role.
     * Response Code: 403
     *
     * @throws Exception if any other internal server error occurs.
     * Response Code: 500
     */
    @HasId
    @DeleteMapping("/{userId}/deliveryAddresses/{deliveryAddressId}")
    public ResponseEntity<ResponseDto> deleteDeliveryAddress(
            @PathVariable Long userId,
            @PathVariable Long deliveryAddressId) {
        ideliveryAddressService.deleteDeliveryAddress(userId, deliveryAddressId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteDeliveryAddressSuccess));
    }
}

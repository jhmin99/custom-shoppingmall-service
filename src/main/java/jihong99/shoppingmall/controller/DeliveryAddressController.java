package jihong99.shoppingmall.controller;

import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.DeliveryAddressDto;
import jihong99.shoppingmall.dto.ResponseDto;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IDeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static jihong99.shoppingmall.constants.Constants.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeliveryAddressController {
    private final IDeliveryAddressService ideliveryAddressService;

    /**
     * Adds a new delivery address for a user.
     *
     * <p>This endpoint is used to add a new delivery address for a user.</p>
     *
     * @param requestDto DTO object containing the delivery address information
     * @return ResponseEntity<ResponseDto> Response object containing the result of the add operation
     * @success Valid response indicating that the delivery address was successfully added
     * Response Code: 201
     * @exception NotFoundException Thrown if the user with the given ID is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PostMapping("/delivery-address")
    public ResponseEntity<ResponseDto> addDeliveryAddress(@Valid @RequestBody DeliveryAddressDto requestDto) {
        ideliveryAddressService.addDeliveryAddress(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(STATUS_201, MESSAGE_201_createDeliveryAddress));

    }

    /**
     * Updates an existing delivery address for a user.
     *
     * <p>This endpoint is used to update an existing delivery address for a user.</p>
     *
     * @param addressId The ID of the delivery address to be updated
     * @param requestDto DTO object containing the updated delivery address information
     * @return ResponseEntity<ResponseDto> Response object containing the result of the update operation
     * @success Valid response indicating that the delivery address was successfully updated
     * Response Code: 200
     * @exception NotFoundException Thrown if the delivery address with the given ID is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @PutMapping("/delivery-address/{addressId}")
    public ResponseEntity<ResponseDto> updateDeliveryAddress(@PathVariable Long addressId, @Valid @RequestBody DeliveryAddressDto requestDto) {
        ideliveryAddressService.updateDeliveryAddress(addressId, requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_UpdateDeliveryAddressSuccess));

    }

    /**
     * Deletes an existing delivery address for a user.
     *
     * <p>This endpoint is used to delete an existing delivery address for a user.</p>
     *
     * @param addressId The ID of the delivery address to be deleted
     * @return ResponseEntity<ResponseDto> Response object containing the result of the delete operation
     * @success Valid response indicating that the delivery address was successfully deleted
     * Response Code: 200
     * @exception NotFoundException Thrown if the delivery address with the given ID is not found
     * Response Code: 404
     * @exception Exception Internal server error occurred
     * Response Code: 500
     */
    @DeleteMapping("/delivery-address/{addressId}")
    public ResponseEntity<ResponseDto> deleteDeliveryAddress(@PathVariable Long addressId) {
        ideliveryAddressService.deleteDeliveryAddress(addressId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(STATUS_200, MESSAGE_200_DeleteDeliveryAddressSuccess));

    }

}
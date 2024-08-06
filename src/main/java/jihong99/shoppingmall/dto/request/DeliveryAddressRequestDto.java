package jihong99.shoppingmall.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for delivery address.
 */
@Getter
@AllArgsConstructor
public class DeliveryAddressRequestDto {

    /**
     * Delivery address ID.
     */
    private Long id;

    /**
     * Name of the recipient.
     *
     * <p>This field is mandatory and must be between 5 and 100 characters.</p>
     */
    @NotNull(message = "Name is a required field.")
    @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
    private String name;

    /**
     * Phone number of the recipient.
     *
     * <p>This field is mandatory and must be between 10 and 15 characters.</p>
     */
    @NotNull(message = "Phone number is a required field.")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters.")
    private String phoneNumber;

    /**
     * Zip code of the delivery address.
     *
     * <p>This field is mandatory.</p>
     */
    @NotNull(message = "Zip code is a required field.")
    private Integer zipCode;

    /**
     * Delivery address.
     *
     * <p>This field is mandatory and must be between 5 and 200 characters.</p>
     */
    @NotNull(message = "Address is a required field.")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters.")
    private String address;

    /**
     * Detailed delivery address.
     *
     * <p>This field is mandatory and must be between 5 and 200 characters.</p>
     */
    @NotNull(message = "Address detail is a required field.")
    @Size(min = 5, max = 200, message = "Address detail must be between 5 and 200 characters.")
    private String addressDetail;
}

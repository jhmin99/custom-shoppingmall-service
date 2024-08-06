package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a delivery address for a user.
 */
@Entity @Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DeliveryAddress extends BaseEntity {

    /**
     * Unique identifier for the delivery address.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_address_id")
    private Long id;

    /**
     * User associated with the delivery address.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * Name associated with the delivery address.
     */
    private String name;

    /**
     * Phone number associated with the delivery address.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Zip code of the delivery address.
     */
    @Column(name = "zip_code")
    private Integer zipCode;

    /**
     * Main address line.
     */
    private String address;

    /**
     * Detailed address information.
     */
    @Column(name = "address_detail")
    private String addressDetail;

    /**
     * Updates the name associated with the delivery address.
     *
     * @param name the new name
     */
    public void updateName(String name){
        this.name = name;
    }

    /**
     * Updates the phone number associated with the delivery address.
     *
     * @param phoneNumber the new phone number
     */
    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    /**
     * Updates the zip code of the delivery address.
     *
     * @param zipCode the new zip code
     */
    public void updateZipCode(Integer zipCode){
        this.zipCode = zipCode;
    }

    /**
     * Updates the main address line.
     *
     * @param address the new address
     */
    public void updateAddress(String address){
        this.address = address;
    }

    /**
     * Updates the detailed address information.
     *
     * @param addressDetail the new detailed address information
     */
    public void updateAddressDetail(String addressDetail){
        this.addressDetail = addressDetail;
    }

    /**
     * Creates a new DeliveryAddress instance.
     *
     * @param users the user associated with the delivery address
     * @param name the name associated with the delivery address
     * @param phoneNumber the phone number associated with the delivery address
     * @param zipCode the zip code of the delivery address
     * @param address the main address line
     * @param addressDetail the detailed address information
     * @return a new DeliveryAddress instance
     */
    public static DeliveryAddress createDeliveryAddress(Users users, String name, String phoneNumber, Integer zipCode,
                                                        String address, String addressDetail){
        return DeliveryAddress.builder()
                .users(users)
                .name(name)
                .phoneNumber(phoneNumber)
                .zipCode(zipCode)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }
}

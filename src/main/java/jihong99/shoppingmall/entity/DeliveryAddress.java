package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryAddressId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    private String name;

    private String phoneNumber;

    private Integer zipCode;

    private String address;

    private String addressDetail;


    public void updateName(String name){this.name = name;}
    public void updatePhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
    public void updateZipCode(Integer zipCode){
        this.zipCode = zipCode;
    }

    public void updateAddress(String address){
        this.address = address;
    }

    public void updateAddressDetail(String addressDetail){
        this.addressDetail = addressDetail;
    }

}

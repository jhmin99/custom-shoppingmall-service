package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddress extends BaseEntity {

    // 배송지 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deliveryAddressId")
    private Long id;

    // 회원 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    // 수령인 이름
    private String name;

    // 수령인 핸드폰 번호
    private String phoneNumber;

    // 수령인 우편번호
    private Integer zipCode;

    // 수령인 주소
    private String address;

    // 수령인 주소 상세
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

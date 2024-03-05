package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.Tier;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;


@Entity
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "User",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UniqueCartAndWishList",
                        columnNames = {
                                "cart_id",
                                "wish_list_id"
                        }
                )
        }
)
public class User extends BaseEntity {

    // 회원 번호
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // 장바구니 번호 (fk)
    @OneToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 찜 번호 (fk)
    @OneToOne
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    // 아이디
    private String identification;

    // 비밀번호
    private String password;

    // 이름
    private String name;

    // 생년월일
    private LocalDate birthDate;

    // 핸드폰 번호
    private String phoneNumber;

    /**
     * 포인트
     * default : 0
     */
    private Integer point;

    /**
     * 등급
     * default : IRON
     */
    @Enumerated(EnumType.STRING)
    private Tier tier;

    /**
     * 다음 등급까지 남은 금액
     * default : 50000
     */
    private Integer amountToNextTier;

    // 등록 날짜
    @CreatedDate
    private LocalDate registrationDate;

    // 장바구니 생성
    public void createCart(Cart cart){
        this.cart = cart;
    }

    // 찜 생성
    public void createWishList(WishList wishList){
        this.wishList = wishList;
    }

    // 핸드폰 번호 수정
    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    // 포인트 업데이트
    public void updatePoint(Integer point){
        this.point = point;
    }

    // 등급 업데이트
    public void updateTier(Tier tier){
        this.tier = tier;
    }

    // 다음 등급까지 남은 금액 업데이트
    public void updateAmountToNextTier(Integer amountToNextTier){
        this.amountToNextTier = amountToNextTier;
    }

}

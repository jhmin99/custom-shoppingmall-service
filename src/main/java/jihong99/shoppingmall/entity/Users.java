package jihong99.shoppingmall.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.Roles;
import jihong99.shoppingmall.entity.enums.Tiers;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Table(
        name = "Users",
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
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Users extends BaseEntity {

    /**
     * Primary key for the user entity
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * The cart associated with the user.
     * It is a foreign key referencing the cart entity.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * The wish list associated with the user.
     * It is a foreign key referencing the wish list entity.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wish_list_id")
    private WishList wishList;

    /**
     * The user's identification.
     */
    private String identification;

    /**
     * The user's password.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * The user's name.
     */
    private String name;

    /**
     * The user's date of birth.
     */
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    private String phoneNumber;

    /**
     * The user's points.
     * Default value: 0
     */
    private Integer point;

    /**
     * The user's tier.
     * Default value: IRON
     */
    @Enumerated(EnumType.STRING)
    private Tiers tier;

    /**
     * The remaining amount until the next tier.
     * Default value: 50000
     */
    private Integer amountToNextTier;

    /**
     * Role: "user"
     */
    @Enumerated(EnumType.STRING)
    private Roles role;

    /**
     * The registration date of the user.
     */
    @CreatedDate
    private LocalDate registrationDate;

    /**
     * The refresh token for the user.
     *
     * <p>This field stores the refresh token issued to the user. The refresh token is used to obtain
     * a new access token without requiring the user to re-authenticate. It is stored as a plain string.</p>
     */
    private String refreshToken;

    /**
     * Constructs a new user with the provided information.
     *
     * @param identification the user's identification
     * @param password the user's password
     * @param name the user's name
     * @param birthDate the user's birth date
     * @param phoneNumber the user's phone number
     */
    @Builder
    public Users(String identification, String password, String name, LocalDate birthDate, String phoneNumber){
        this.identification = identification;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    public void updateCart(Cart cart){
        this.cart = cart;
    }

    public void updateWishList(WishList wishList){
        this.wishList = wishList;
    }

    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void updatePoint(Integer point){
        this.point = point;
    }

    public void updateTier(Tiers tier){
        this.tier = tier;
    }

    public void updateRole(Roles role){ this.role = role;}

    public void updatePassword(String password){this.password = password;}

    public void updateAmountToNextTier(Integer amountToNextTier){
        this.amountToNextTier = amountToNextTier;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "Users{" +
                "identification='" + identification + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

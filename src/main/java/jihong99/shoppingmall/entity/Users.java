package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.Tier;
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
    private Tier tier;

    /**
     * The remaining amount until the next tier.
     * Default value: 50000
     */
    private Integer amountToNextTier;

    /**
     * The registration date of the user.
     */
    @CreatedDate
    private LocalDate registrationDate;


    /**
     * Constructs a new user with the provided information.
     *
     * @param identification The user's identification
     * @param password The user's password
     * @param name The user's name
     * @param birthDate The user's birth date
     * @param phoneNumber The user's phone number
     */
    @Builder
    public Users(String identification, String password, String name, LocalDate birthDate, String phoneNumber){
        this.identification = identification;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Updates the user's shopping cart.
     *
     * @param cart The new shopping cart to be associated with the user
     */
    public void updateCart(Cart cart){
        this.cart = cart;
    }

    /**
     * Updates the user's wishlist.
     *
     * @param wishList The new wishlist to be associated with the user
     */
    public void updateWishList(WishList wishList){
        this.wishList = wishList;
    }

    /**
     * Updates the user's phone number.
     *
     * @param phoneNumber The new phone number to be associated with the user
     */
    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    /**
     * Updates the user's point balance.
     *
     * @param point The new point balance to be associated with the user
     */
    public void updatePoint(Integer point){
        this.point = point;
    }

    /**
     * Updates the user's tier.
     *
     * @param tier The new tier to be associated with the user
     */
    public void updateTier(Tier tier){
        this.tier = tier;
    }

    /**
     * Updates the amount of money remaining until the next tier.
     *
     * @param amountToNextTier The new amount to be associated with the user
     */
    public void updateAmountToNextTier(Integer amountToNextTier){
        this.amountToNextTier = amountToNextTier;
    }
}

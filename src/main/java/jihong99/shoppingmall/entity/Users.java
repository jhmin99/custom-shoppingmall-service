package jihong99.shoppingmall.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import jihong99.shoppingmall.entity.enums.Roles;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

/**
 * Represents a user in the shopping mall system.
 *
 * <p>The Users entity stores information about a user, including their cart, wish list, identification, password, name, birth date, phone number, role, registration date, and refresh token.</p>
 */
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
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users extends BaseEntity {

    /**
     * Primary key for the user entity.
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
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * The user's phone number.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * The user's role.
     */
    @Enumerated(EnumType.STRING)
    private Roles role;

    /**
     * The user's refresh token.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * Creates a new user with the given details.
     *
     * @param identification The user's identification
     * @param password The user's password
     * @param name The user's name
     * @param birthDate The user's date of birth
     * @param phoneNumber The user's phone number
     * @return A new user instance
     */
    public static Users of(String identification, String password, String name, LocalDate birthDate, String phoneNumber){
        return Users.builder()
                .identification(identification)
                .password(password)
                .name(name)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .role(Roles.USER)
                .build();
    }

    /**
     * Creates a new admin user with the given details.
     *
     * @param identification The admin's identification
     * @param password The admin's password
     * @param name The admin's name
     * @param birthDate The admin's date of birth
     * @param phoneNumber The admin's phone number
     * @return A new admin user instance
     */
    public static Users ofAdmin(String identification, String password, String name, LocalDate birthDate, String phoneNumber){
        return Users.builder()
                .identification(identification)
                .password(password)
                .name(name)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .role(Roles.ADMIN)
                .build();
    }

    /**
     * Creates a new super admin user with the given details.
     *
     * @param identification The super admin's identification
     * @param password The super admin's password
     * @return A new super admin user instance
     */
    public static Users ofSuperAdmin(String identification, String password){
        return Users.builder()
                .identification(identification)
                .password(password)
                .role(Roles.SUPER_ADMIN)
                .build();
    }

    /**
     * Updates the user's cart.
     *
     * @param cart The new cart to be associated with the user
     */
    public void updateCart(Cart cart){
        this.cart = cart;
    }

    /**
     * Updates the user's wish list.
     *
     * @param wishList The new wish list to be associated with the user
     */
    public void updateWishList(WishList wishList){
        this.wishList = wishList;
    }

    /**
     *
     * Updates the user's name.
     * @param name - The new username
     */
    public void updateName (String name) {
        this.name = name;
    }
    /**
     * Updates the user's phone number.
     *
     * @param phoneNumber The new phone number
     */
    public void updatePhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    /**
     * Updates the user's role.
     *
     * @param role The new role
     */
    public void updateRole(Roles role){
        this.role = role;
    }

    /**
     * Updates the user's password.
     *
     * @param password The new password
     */
    public void updatePassword(String password){
        this.password = password;
    }

    /**
     * Updates the user's refresh token.
     *
     * @param refreshToken The new refresh token
     */
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}

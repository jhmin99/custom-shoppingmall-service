package jihong99.shoppingmall.dto;

import jihong99.shoppingmall.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@AllArgsConstructor
public class UserDetailsDto implements UserDetails {
    private final Users user;

    /**
     * Returns the authorities granted to the user.
     *
     * <p>This implementation uses the role of the user stored in the {@link Users} entity
     * to provide a {@link SimpleGrantedAuthority}.</p>
     *
     * @return a collection containing the user's authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the user's password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the user's identification
     */
    @Override
    public String getUsername() {
        return user.getIdentification();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true, as this implementation does not track account expiration
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true, as this implementation does not track account locking
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return true, as this implementation does not track credential expiration
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true, as this implementation does not track account enabling/disabling
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Returns the user entity.
     *
     * @return the user entity associated with this UserDetails
     */
    public Users getUser() {
        return user;
    }
}

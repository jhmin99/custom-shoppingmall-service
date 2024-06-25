package jihong99.shoppingmall.entity.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum representing the roles in the application.
 *
 * <p>This enum implements {@link GrantedAuthority} to be used by Spring Security for
 * authorization purposes.</p>
 */
public enum Roles implements GrantedAuthority {
    USER, ADMIN;

    /**
     * Returns the authority granted to the role.
     *
     * <p>This method is used by Spring Security to get the name of the role as a
     * granted authority.</p>
     *
     * @return the name of the role
     */
    @Override
    public String getAuthority() {
        return name();
    }
}

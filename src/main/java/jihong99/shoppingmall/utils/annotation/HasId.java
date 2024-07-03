package jihong99.shoppingmall.utils.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Custom annotation to check if the authenticated user has the specified user ID.
 *
 * <p>This annotation is used to ensure that the authenticated user has the same ID
 * as the user ID provided in the method parameter. It leverages the `PreAuthorize`
 * annotation from Spring Security to apply the authorization logic defined in the
 * `hasId` method of `authServiceImpl`.</p>
 *
 * @precondition The user must be authenticated and must have the specified user ID.
 * Response Code: 403 (Forbidden) if the precondition fails.
 *
 * @see PreAuthorize This annotation is used to apply method-level security based on the provided SpEL expression.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@authServiceImpl.hasId(#userId)")
public @interface HasId {
}

package jihong99.shoppingmall.config.auth.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * This method is called for every request to ensure CSRF token is added to the response headers.
 *
 * <p>The filter retrieves the CSRF token from the request attributes and sets it as a response header.
 * This is important for securing the application against Cross-Site Request Forgery (CSRF) attacks.</p>
 *
 * @param request  the HttpServletRequest object
 * @param response the HttpServletResponse object
 * @param filterChain the FilterChain object
 * @throws ServletException if an error occurs during the servlet processing
 * @throws IOException if an input or output error is detected
 */
@Component
public class CsrfCookieFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if(null != csrfToken.getHeaderName()){
            Cookie cookie = new Cookie("XSRF-TOKEN", csrfToken.getToken());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        filterChain.doFilter(request, response);
    }
}

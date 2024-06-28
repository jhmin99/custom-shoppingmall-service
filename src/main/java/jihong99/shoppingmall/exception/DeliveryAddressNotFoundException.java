package jihong99.shoppingmall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class DeliveryAddressNotFoundException extends RuntimeException {
    public DeliveryAddressNotFoundException(String message) {
        super(message);
    }
}

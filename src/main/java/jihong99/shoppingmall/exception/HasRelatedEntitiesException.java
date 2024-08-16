package jihong99.shoppingmall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class HasRelatedEntitiesException extends RuntimeException{

    public HasRelatedEntitiesException(String message){
        super(message);
    }
}

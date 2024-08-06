package jihong99.shoppingmall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class HasRelationException extends RuntimeException{

    public HasRelationException(String message){
        super(message);
    }
}

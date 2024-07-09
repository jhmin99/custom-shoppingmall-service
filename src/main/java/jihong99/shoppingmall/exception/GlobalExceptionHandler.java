package jihong99.shoppingmall.exception;

import jihong99.shoppingmall.dto.ErrorResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GlobalExceptionHandler handles various exceptions thrown by the application
 * and returns appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles all exceptions that are not specifically handled by other methods.
     *
     * @param exception The exception to be handled.
     * @param webRequest The web request.
     * @return The ResponseEntity containing the ErrorResponseDto.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(webRequest, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors in request bodies.
     *
     * @param exception The exception to be handled.
     * @param headers The HTTP headers.
     * @param status The HTTP status code.
     * @param request The web request.
     * @return The ResponseEntity containing the validation errors.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = exception.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName, validationMsg);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.FORBIDDEN, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles not found exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(NotFoundException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.NOT_FOUND, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles date time parse exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorResponseDto> handleDateTimeParseException(DateTimeParseException exception, WebRequest request) {
        String errorMessage = "Invalid date format: " + exception.getParsedString();
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles password mismatch exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handlePasswordMismatchException(PasswordMismatchException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles duplicate identification exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateIdentificationException(DuplicateNameException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles bad credentials exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(BadCredentialsException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles invalid token exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTokenException(InvalidTokenException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles invalid expiration date Exception.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(InvalidExpirationDateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidExpirationDateException(InvalidExpirationDateException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    /**
     * Builds an ErrorResponseDto with the given details.
     *
     * @param request The web request.
     * @param status The HTTP status code.
     * @param message The error message.
     * @return The built ErrorResponseDto.
     */
    private ErrorResponseDto buildErrorResponseDto(WebRequest request, HttpStatus status, String message) {
        return new ErrorResponseDto(
                request.getDescription(false),
                status,
                message,
                LocalDateTime.now()
        );
    }
}

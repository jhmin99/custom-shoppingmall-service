package jihong99.shoppingmall.exception;

import jihong99.shoppingmall.dto.response.shared.ErrorResponseDto;
import org.hibernate.TypeMismatchException;
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
import java.util.ArrayList;
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(webRequest, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), null);
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
        List<Map<String, String>> validationErrors = new ArrayList<>();
        List<ObjectError> validationErrorList = exception.getBindingResult().getAllErrors();

        validationErrorList.forEach((error) -> {
            Map<String, String> errorDetails = new HashMap<>();
            String fieldName = ((FieldError) error).getField();
            String validationMsg = error.getDefaultMessage();
            errorDetails.put("fieldName", fieldName);
            errorDetails.put("validationMsg", validationMsg);
            validationErrors.add(errorDetails);
        });
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), validationErrors);

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.FORBIDDEN, exception.getMessage(), null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.NOT_FOUND, exception.getMessage(), null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, errorMessage, null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
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
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles invalid expiration date exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOperationException(InvalidOperationException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles type mismatch exceptions.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatchException(TypeMismatchException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }
    /**
     * Handles exceptions where an entity cannot be deleted due to existing related entities.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(HasRelatedEntitiesException.class)
    public ResponseEntity<ErrorResponseDto> handleHasRelatedEntitiesException(HasRelatedEntitiesException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.CONFLICT, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.CONFLICT);
    }

    /**
     * Handles an exception that occurs when an illegal argument is passed.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.BAD_REQUEST, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exceptions that occur during image upload.
     *
     * @param exception The exception to be handled.
     * @param request The web request.
     * @return The ResponseEntity containing the error message.
     */
    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponseDto> handleImageUploadException(ImageUploadException exception, WebRequest request) {
        ErrorResponseDto errorResponseDto = buildErrorResponseDto(request, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), null);
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds an ErrorResponseDto with the given details.
     *
     * @param request The web request.
     * @param status The HTTP status code.
     * @param message The error message.
     * @param validationErrors The validation errors.
     * @return The built ErrorResponseDto.
     */
    private ErrorResponseDto buildErrorResponseDto(WebRequest request, HttpStatus status, String message, List<Map<String, String>> validationErrors) {
        return new ErrorResponseDto(
                request.getDescription(false),
                status,
                message,
                LocalDateTime.now(),
                validationErrors
        );
    }



}

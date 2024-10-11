package jihong99.shoppingmall.controller;


import jakarta.validation.Valid;
import jihong99.shoppingmall.dto.request.review.ReviewRequestDto;
import jihong99.shoppingmall.dto.response.shared.ResponseDto;
import jihong99.shoppingmall.exception.InvalidOperationException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.service.IReviewService;
import jihong99.shoppingmall.utils.annotation.HasId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static jihong99.shoppingmall.constants.Constants.*;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ReviewController {
    private final IReviewService ireviewService;

    /**
     * Submits a new review for an item.
     *
     * <p>This endpoint allows a user to submit a review for a specific item. The review details, such as rating, title,
     * and content, are provided in the request body. Optionally, the user can upload images associated with the review.
     * The review is linked to both the user and the item being reviewed.</p>
     *
     * @param userId The ID of the user submitting the review
     * @param itemId The ID of the item being reviewed
     * @param reviewRequestDto The DTO containing the review details (title, content, rating)
     * @param images A list of images to be associated with the review
     * @return A ResponseEntity indicating the result of the review submission
     *
     * @success Review successfully submitted
     * Response Code: 201
     *
     * @throws MethodArgumentNotValidException if the review data is invalid (e.g., missing required fields)
     * Response Code: 400
     *
     * @throws InvalidOperationException if the review is invalid for any reason
     * Response Code: 400
     *
     * @throws AccessDeniedException if the user does not have the appropriate role or access
     * Response Code: 403
     *
     * @throws NotFoundException if the user or item is not found
     * Response Code: 404
     *
     * @throws Exception if any other internal server error occurs
     * Response Code: 500
     */
    @PostMapping(path = "/users/{userId}/items/{itemId}/reviews", consumes = MULTIPART_FORM_DATA_VALUE)
    @HasId
    public ResponseEntity<ResponseDto> createReview(@PathVariable Long userId,
                                                    @PathVariable Long itemId,
                                                    @Valid @RequestPart("review") ReviewRequestDto reviewRequestDto,
                                                    @RequestPart("images") List<MultipartFile> images
                                                    ){
        ireviewService.createReview(userId, itemId, reviewRequestDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseDto(STATUS_201, MESSAGE_201_createReviewSuccess)
        );
    }

}

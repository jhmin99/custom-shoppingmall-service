package jihong99.shoppingmall.service;


import jihong99.shoppingmall.dto.request.review.ReviewRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReviewService {
    void createReview(Long userId, Long itemId, ReviewRequestDto reviewRequestDto, List<MultipartFile> images);
}

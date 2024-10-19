package jihong99.shoppingmall.service;


import jihong99.shoppingmall.dto.request.review.ReviewRequestDto;
import jihong99.shoppingmall.entity.Image;
import jihong99.shoppingmall.entity.Item;
import jihong99.shoppingmall.entity.Review;
import jihong99.shoppingmall.entity.Users;
import jihong99.shoppingmall.exception.ImageUploadException;
import jihong99.shoppingmall.exception.NotFoundException;
import jihong99.shoppingmall.repository.ImageRepository;
import jihong99.shoppingmall.repository.ItemRepository;
import jihong99.shoppingmall.repository.ReviewRepository;
import jihong99.shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static jihong99.shoppingmall.constants.Constants.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService{
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public void createReview(Long userId, Long itemId, ReviewRequestDto reviewRequestDto, List<MultipartFile> images) {
        Users user = findUserOrThrow(userId);
        Item item = findItemOrThrow(itemId);
        List<Image> imageEntities = uploadAndPersistImages(images);
        createAndSaveReview(reviewRequestDto, user, item, imageEntities);
    }

    private void createAndSaveReview(ReviewRequestDto reviewRequestDto, Users user, Item item, List<Image> imageEntities) {
        Review review = buildReviewEntity(reviewRequestDto, user, item, imageEntities);
        reviewRepository.save(review);
    }

    private static Review buildReviewEntity(ReviewRequestDto reviewRequestDto, Users user, Item item, List<Image> imageEntities) {
        return Review.of(user,
                item,
                reviewRequestDto.getTitle(),
                reviewRequestDto.getContent(),
                reviewRequestDto.getRating(),
                imageEntities);
    }

    private Users findUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_UserNotFound)
        );
    }

    private Item findItemOrThrow(Long itemId){
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(MESSAGE_404_ItemNotFound)
        );
    }



    private List<Image> uploadAndPersistImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(this::uploadAndPersistSingleImage)
                .collect(Collectors.toList());
    }


    private Image uploadAndPersistSingleImage(MultipartFile image) {
        try {
            String imageUrl = fileStorageService.uploadFile(image.getOriginalFilename(), image.getInputStream(), image.getContentType());
            Image savedImage = Image.of(image, imageUrl);
            return imageRepository.save(savedImage);
        } catch (IOException e) {
            throw new ImageUploadException(MESSAGE_500_ImageUploadFailed);
        }
    }
}

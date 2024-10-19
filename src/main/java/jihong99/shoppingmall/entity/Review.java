package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a review for an item in the shopping mall system.
 *
 * <p>The Review entity stores information about a user's review for an item,
 * including the rating, title, content, and associated images.</p>
 */
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review extends BaseEntity {

    /**
     * Unique identifier for the review.
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    /**
     * The user who wrote the review.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /**
     * The item being reviewed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * The rating given to the item.
     */
    private Integer rating;

    /**
     * The title of the review.
     */
    private String title;

    /**
     * The content of the review.
     */
    private String content;

    /**
     * Indicates if the review contains photos.
     */
    @Column(name = "has_photo")
    private Boolean hasPhoto = false;

    /**
     * List of images associated with the review.
     */
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();


    @CreatedDate
    @Column(name = "registration_date")
    private LocalDate registrationDate;

    /**
     * Creates a new review with the provided details.
     *
     * @param users The user who wrote the review
     * @param item The item being reviewed
     * @param title The title of the review
     * @param content The content of the review
     * @param rating The rating given to the item
     * @param images List of images associated with the review
     * @return A new Review instance
     */
    public static Review of(Users users, Item item, String title, String content, Integer rating, List<Image> images) {
        Review review = Review.builder()
                .users(users)
                .item(item)
                .title(title)
                .content(content)
                .rating(rating)
                .build();

        if (images != null) {
            for (Image image : images) {
                image.setReview(review);
            }
            review.images = images;
            review.hasPhoto = true;
        }

        return review;
    }

}

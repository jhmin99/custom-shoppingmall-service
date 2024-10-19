package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.multipart.MultipartFile;

/**
 * Represents an image associated with an item or a review.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Image extends BaseEntity {

    /**
     * Unique identifier for the image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    /**
     * Item associated with the image.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * Review associated with the image.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    /**
     * Name of the image file.
     */
    private String name;

    /**
     * URL where the image is stored.
     */
    private String url;

    /**
     * MIME type of the image file.
     */
    private String type;

    /**
     * Size of the image file in bytes.
     */
    private Long size;
    public static Image of(MultipartFile image, String imageUrl) {
        return Image.builder()
                .name(image.getOriginalFilename())
                .url(imageUrl)
                .type(image.getContentType())
                .size(image.getSize())
                .build();
    }

    /**
     * Sets the review associated with the image.
     *
     * @param review the review to associate with the image
     */
    public void setReview(Review review) {
        this.review = review;
    }

    /**
     * Sets the item associated with the image.
     *
     * @param item the item to associate with the image
     */
    public void setItem(Item item) {
        this.item = item;
    }
}

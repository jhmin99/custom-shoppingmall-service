package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in the shopping mall.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Item extends BaseEntity {

    /**
     * Unique identifier for the item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    /**
     * Name of the item.
     */
    private String name;

    /**
     * Price of the item.
     */
    private Long price;

    /**
     * Stock quantity of the item.
     */
    private Integer stock;

    /**
     * Keyword associated with the item.
     */
    private String keyword;

    /**
     * Indicates if the item is invalid.
     */
    @Column(name = "is_invalid")
    private boolean isInvalid;

    /**
     * Average rating of the item.
     */
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating = BigDecimal.ZERO;

    /**
     * Total rating sum of the item.
     */
    @Column(name = "total_rating", precision = 10, scale = 2)
    private BigDecimal totalRating = BigDecimal.ZERO;

    /**
     * Count of ratings the item has received.
     */
    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    /**
     * Images associated with the item.
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    /**
     * Updates the name of the item.
     *
     * @param name the new name of the item
     */
    public void updateName(String name) {
        this.name = name;
    }

    /**
     * Updates the price of the item.
     *
     * @param price the new price of the item
     */
    public void updatePrice(Long price) {
        this.price = price;
    }

    /**
     * Updates the stock quantity of the item.
     *
     * @param stock the new stock quantity of the item
     */
    public void updateStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Updates the keyword associated with the item.
     *
     * @param keyword the new keyword for the item
     */
    public void updateKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Adds a new rating to the item and updates the average rating.
     *
     * @param rating the new rating to add
     */
    public void addRating(BigDecimal rating) {
        this.totalRating = this.totalRating.add(rating);
        this.ratingCount++;
        this.averageRating = this.totalRating.divide(BigDecimal.valueOf(this.ratingCount), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Adds an image to the item.
     *
     * @param image the image to add
     */
    public void addImage(Image image) {
        image.setItem(this);
        images.add(image);
    }

    /**
     * Removes an image from the item.
     *
     * @param image the image to remove
     */
    public void removeImage(Image image) {
        images.remove(image);
        image.setItem(null);
    }

    public void invalidateItem(){
        this.isInvalid = true;
    }
    public void validateItem(){
        this.isInvalid = false;
    }
    /**
     * Creates a new item with the specified properties.
     *
     * @param name the name of the item
     * @param price the price of the item
     * @param stock the stock quantity of the item
     * @param keyword the keyword associated with the item
     * @param images the list of images associated with the item
     * @return a new Item instance
     */
    public static Item of(String name, Long price, Integer stock, String keyword, List<Image> images) {
        Item item = Item.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .keyword(keyword)
                .isInvalid(false)
                .build();

        if (images != null) {
            for (Image image : images) {
                image.setItem(item);
            }
            item.images = images;
        }

        return item;
    }
}

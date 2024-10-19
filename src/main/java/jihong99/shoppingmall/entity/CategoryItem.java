package jihong99.shoppingmall.entity;

import jakarta.persistence.*;
import jihong99.shoppingmall.entity.base.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents the relationship between a category and an item in the shopping mall.
 *
 * <p>The CategoryItem entity associates an item with a specific category.</p>
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CategoryItem extends BaseEntity {

    /**
     * Unique identifier for the category-item relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_item_id")
    private Long id;

    /**
     * The item associated with this category-item relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * The category associated with this category-item relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public static CategoryItem of(Item item, Category category) {
        return CategoryItem.builder()
                .category(category)
                .item(item).build();
    }
}
